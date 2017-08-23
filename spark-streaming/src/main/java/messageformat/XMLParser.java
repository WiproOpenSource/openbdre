package messageformat;

import org.json.JSONException;
import xmlparsing.XML;

public class XMLParser{
    public static void main(String[] args) throws JSONException{
        System.out.println(" json= "+ XML.toJSONObject("<Deal>         <Header>             <BulkId>MIG01</BulkId>             <ProcModeCd>1</ProcModeCd>                   <BusinessKey>bk3</BusinessKey>                   <SourceTimeStamp>2016-01-25T09:32:01+01:00</SourceTimeStamp>                   <SourceSystemID>S1</SourceSystemID>              </Header>         <Id>id1</Id>             <AccNo>CITINA000000001</AccNo>         <Category>2</Category>               <CtgyCd>1</CtgyCd>         <Status>100</Status>         <ExtTpDesc>DEP</ExtTpDesc>               <Desc>Deal Details</Desc>         <OrderDate>2016-01-25T09:32:01+01:00</OrderDate>         <CreatedAt>2016-01-25T09:32:01+01:00</CreatedAt>         <DealLink>             <Id>TEST20150808000010</Id>         </DealLink>     </Deal>").toString());
    }
}