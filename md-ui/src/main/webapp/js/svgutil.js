function saveSVG(fileName,index)
 {
     var textToWrite = '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">' + document.getElementsByTagName("svg")[index].innerHTML+'</svg>';
     var textFileAsBlob = new Blob([textToWrite], {type:'text/plain'});
     var fileNameToSaveAs = fileName + ".svg";

     var downloadLink = document.createElement("a");
     downloadLink.download = fileNameToSaveAs;
     downloadLink.innerHTML = "Download File";
     if (window.webkitURL != null)
     {
         // Chrome allows the link to be clicked
         // without actually adding it to the DOM.
         downloadLink.href = window.webkitURL.createObjectURL(textFileAsBlob);
     }
     else
     {
         // Firefox requires the link to be added to the DOM
         // before it can be clicked.
         downloadLink.href = window.URL.createObjectURL(textFileAsBlob);
         downloadLink.onclick = destroyClickedElement;
         downloadLink.style.display = "none";
         document.body.appendChild(downloadLink);
     }

     downloadLink.click();
 }