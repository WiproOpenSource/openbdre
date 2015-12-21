/**
 * (c) Copyright IBM Corp. 2013. All rights reserved.
 *
 * Portions of this software were originally based on the following:
 *  Apache Mahout: http://mahout.apache.org/
 *  Cloud9: https://github.com/lintool/Cloud9
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.io.xml;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapred.*;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Reads records that are delimited by a specifc begin/end tag.
 *
 * @author Jimmy Lin (support for compression)
 * @author Dmitry Vasilenko (support for Apache HCatalog/Hive)
 */
public class XmlInputFormat extends TextInputFormat {

    public static final String START_TAG_KEY = "xmlinput.start";
    public static final String END_TAG_KEY = "xmlinput.end";

    @Override
    public RecordReader<LongWritable, Text> getRecordReader(InputSplit inputSplit, JobConf jobConf, Reporter reporter) throws IOException {
        return new XmlRecordReader((FileSplit) inputSplit, jobConf);
    }

    public static class XmlRecordReader implements RecordReader<LongWritable, Text> {
        private byte[] startTag;
        private byte[] endTag;
        private final long start;
        private final long end;
        private long pos;
        private DataInputStream fsin;
        private DataOutputBuffer buffer = new DataOutputBuffer();
        private long recordStartPos;

        public XmlRecordReader(FileSplit input, JobConf jobConf) throws IOException {
            Configuration conf = jobConf;
            this.startTag = conf.get(START_TAG_KEY).getBytes("utf-8");
            this.endTag = conf.get(END_TAG_KEY).getBytes("utf-8");
            FileSplit split = (FileSplit) input;

            Path file = split.getPath();
            CompressionCodecFactory compressionCodecs = new CompressionCodecFactory(conf);
            CompressionCodec codec = compressionCodecs.getCodec(file);
            FileSystem fs = file.getFileSystem(conf);
            if (codec != null) {
                this.fsin = new DataInputStream(codec.createInputStream(fs.open(file)));
                //Data read only happens in first split and invalid other splits.
                //This is to avoid reading duplicate data for compressed files.
                this.start = (split.getStart() == 0) ? 0 : Long.MAX_VALUE;
                this.end = Long.MAX_VALUE;
            } else {
                this.start = split.getStart();
                this.end = this.start + split.getLength();
                FSDataInputStream fileIn = fs.open(file);
                fileIn.seek(this.start);
                this.fsin = fileIn;
            }
            this.recordStartPos = this.start;
            this.pos = this.start;
        }

        @Override
        public boolean next(LongWritable key, Text value) throws IOException {
            if (this.pos < this.end) {
                if (readUntilMatch(this.startTag, false)) {
                    this.recordStartPos = this.pos - this.startTag.length;
                    try {
                        this.buffer.write(this.startTag);
                        if (readUntilMatch(this.endTag, true)) {
                            key.set(this.recordStartPos);
                            value.set(this.buffer.getData(), 0, this.buffer.getLength());
                            return true;
                        }
                    } finally {
                        if (this.fsin instanceof Seekable) {
                            if (this.pos != ((Seekable) this.fsin).getPos()) {
                                throw new RuntimeException("bytes consumed error!");
                            }
                        }
                        this.buffer.reset();
                    }
                }
            }
            return false;
        }

        @Override
        public LongWritable createKey() {
            return new LongWritable();
        }

        @Override
        public Text createValue() {
            return new Text();
        }

        @Override
        public void close() throws IOException {
            this.fsin.close();
        }

        @Override
        public float getProgress() throws IOException {
            return ((float) (this.pos - this.start)) / ((float) (this.end - this.start));
        }

        private boolean readUntilMatch(byte[] match, boolean withinBlock) throws IOException {
            int i = 0;
            while (true) {
                int b = this.fsin.read();
                ++this.pos;

                if (b == -1) {
                    return false;
                }
                if (withinBlock) {
                    this.buffer.write(b);
                }
                if (b == match[i]) {
                    i++;
                    if (i >= match.length) {
                        return true;
                    }
                } else {
                    i = 0;
                }
                if (!withinBlock && i == 0 && this.pos >= this.end) {
                    return false;
                }
            }
        }

        @Override
        public long getPos() throws IOException {
            return this.pos;
        }
    }
}