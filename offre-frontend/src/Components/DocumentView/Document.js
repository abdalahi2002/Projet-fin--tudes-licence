import React, { useEffect, useState } from 'react';
import '@fortawesome/fontawesome-free/css/all.min.css';
import { useParams } from 'react-router-dom'; 
import axios from 'axios';
import FileViewer from 'react-file-viewer';

function Document() {
//   const [doc, setDoc] = useState(null);

  const [doc,setDoc]=useState({
    base64File:"",
    objet:""
  })

  const {objet}=useParams();

  useEffect(()=>{
    Afficherpdf()
  },[])

//   useEffect(() => {
//     fetchDocument();
//   }, []);

  const Afficherpdf= async ()=>{
    const pdfa=await axios.get(`http://localhost:9001/pdf/${objet}`)
    setDoc(pdfa.data)
  }



  const downloadDocument = async (base64File, objet) => {
    const fileName = objet;
    if (objet.endsWith('.docx') || objet.endsWith('.doc')) {
      const link = document.createElement('a');
      link.href = `data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,${base64File}`;
      link.target = '_blank';
      link.download = `${fileName}`;
      link.click();
    } else if (objet.endsWith('.pdf')) {
      const link = document.createElement('a');
      link.href = `data:application/pdf;base64,${base64File}`;
      link.target = '_blank';
      link.download = `${fileName}`;
      link.click();
    } else if (objet.endsWith('.xlsx')) {
      const link = document.createElement('a');
      link.href = `data:application/vnd.ms-excel;base64,${base64File}`;
      link.target = '_blank';
      link.download = `${fileName}`;
      link.click();
    }
  };
  
  const renderFileViewer = (objet, base64File) => {
    if (objet.endsWith('.docx') || objet.endsWith('.doc')) {
      return (
        <FileViewer
          style={{ width: 1000, height: 650, margin: 'auto' }}
          fileType="docx"
          filePath={`data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,${base64File}`}
        />
      );
    } else if (objet.endsWith('.pdf')) {
      return (
        <FileViewer
          
          fileType="pdf"
          filePath={`data:application/pdf;base64,${base64File}`}
      
        />
      );
    } else if (objet.endsWith('.xlsx')) {
      return (
        <FileViewer
          fileType="xlsx"
          filePath={`data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,${base64File}`}
        />
      );
    }
  };
  return (
    <div>
      
      {doc && (
        <div key={doc.objet}>
            <div class="flex h-20 w-1000 p-2 ">
            <div class="flex-12 px-10 py-1 0 rounded">
                <svg xmlns="http://www.w3.org/2000/svg" height="4em" viewBox="0 0 512 512"><path d="M0 64C0 28.7 28.7 0 64 0H224V128c0 17.7 14.3 32 32 32H384V304H176c-35.3 0-64 28.7-64 64V512H64c-35.3 0-64-28.7-64-64V64zm384 64H256V0L384 128zM176 352h32c30.9 0 56 25.1 56 56s-25.1 56-56 56H192v32c0 8.8-7.2 16-16 16s-16-7.2-16-16V448 368c0-8.8 7.2-16 16-16zm32 80c13.3 0 24-10.7 24-24s-10.7-24-24-24H192v48h16zm96-80h32c26.5 0 48 21.5 48 48v64c0 26.5-21.5 48-48 48H304c-8.8 0-16-7.2-16-16V368c0-8.8 7.2-16 16-16zm32 128c8.8 0 16-7.2 16-16V400c0-8.8-7.2-16-16-16H320v96h16zm80-112c0-8.8 7.2-16 16-16h48c8.8 0 16 7.2 16 16s-7.2 16-16 16H448v32h32c8.8 0 16 7.2 16 16s-7.2 16-16 16H448v48c0 8.8-7.2 16-16 16s-16-7.2-16-16V432 368z"/></svg>
                
            </div>
                <div class="flex-1 px-10 py-5  rounded">
                    <h4 class="mb-4 text-1xl font-extrabold leading-none tracking-tight text-gray-900 md:text-1xl lg:text-1xl">
                        {doc.objet.substring(0, doc.objet.indexOf('-')).trim()}
                    </h4>
                </div>
                
                <div class="contents">
                    
                    <div class="flex-10 px-10 py-2  ">
                        <button class="inline-flex items-center px-3 py-3 text-sm font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800" onClick={() => downloadDocument(doc.base64File, doc.objet)}>
                        <svg class="fill-current w-4 h-4 mr-2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><path d="M13 8V2H7v6H2l8 8 8-8h-5zM0 18h20v2H0v-2z"/></svg>
                            <span>Télécharger</span>
                        </button></div>
                </div>
            </div>
            
            <div style={{ width: 900, height: 650, margin: 'auto' }}>
              {renderFileViewer(doc.objet, doc.base64File)}
            </div>   
        </div>
      )}
    </div>
  );
}

export default Document;
