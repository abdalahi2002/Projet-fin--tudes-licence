import React, { useState, useEffect } from 'react';
import '@fortawesome/fontawesome-free/css/all.min.css';

import { Link } from 'react-router-dom';

function ResponsiveTable({ filter }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [numRows, setNumRows] = useState(5); // Default number of rows
  const [currentPage, setCurrentPage] = useState(1);

  const [documents, setDocuments] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchDocuments();
  }, []);

  const fetchDocuments = async () => {
    // Call the API to retrieve the list of documents
    const response = await fetch('http://localhost:9001/api/metadata?limit=20'); // Added query parameter to limit to 20 results
    const data = await response.json();
    setDocuments(data);
  };

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
    setSearchTerm(e.target.value);
  };

  const filteredDocuments = documents.filter((document) =>
    document.origine.toLowerCase().includes(searchTerm.toLowerCase()) ||
    document.objet.toLowerCase().includes(searchTerm.toLowerCase()) ||
    document.titre.toLowerCase().includes(searchTerm.toLowerCase()) ||
    document.datePub.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const sortedDocuments = filteredDocuments.sort((a, b) => {
    // Compare the dates (assuming the date property is a string in the format 'YYYY-MM-DD')
    const dateA = new Date(a.date);
    const dateB = new Date(b.date);
    return dateB - dateA;
  });

  const handleNumRowsChange = (e) => {
    setNumRows(parseInt(e.target.value));
  };

  const perPageOptions = [5, 10, ]; // Added options for number of rows

  const totalPages = Math.ceil(filteredDocuments.length / numRows);
  const startIndex = (currentPage - 1) * numRows;
  const endIndex = startIndex + numRows;
  const paginatedDocuments = sortedDocuments.slice(startIndex, endIndex);

  const filteredDocumentsByObject = filter
    ? paginatedDocuments.filter((document) => document.objet.toLowerCase() === filter.toLowerCase())
    : paginatedDocuments;

  return (
    <div className="container mx-auto px-4 py-10">
      <div className="bg-white shadow-md rounded my-6 p-6">
        <div className="flex justify-between items-center mb-6">
          <div className="flex items-center">
            <label htmlFor="search" className="mr-2">Search:</label>
            <input
              id="search"
              type="text"
              value={searchQuery}
              onChange={handleSearchChange}
              className="border border-gray-300 rounded px-2 py-1"
            />
          </div>
          <div className="flex items-center">
            <label htmlFor="numRows" className="mr-2">Rows:</label>
            <select
              id="numRows"
              value={numRows}
              onChange={handleNumRowsChange}
              className="border border-gray-300 rounded px-2 py-1"
            >
              {perPageOptions.map((option) => (
                <option key={option} value={option}>{option}</option>
              ))}
            </select>
          </div>
        </div>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead>
              <tr className="bg-gray-200 text-indigo-500 text-sm leading-normal">
                <th className="py-3 px-0 md:px-6 text-left">Titre</th>
                <th className="py-3 px-3 md:px-6 text-left">Origine</th>
                <th className="py-3 px-3 md:px-6 text-left">Date de Publication</th>
                <th className="py-3 px-3 md:px-6 text-left">Objet</th>
                <th className="py-3 px-3 md:px-6 text-left">Action</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredDocumentsByObject.map((document) => (
                <tr key={document.objet} className="border-b border-gray-200 hover:bg-gray-100">
                  <td className="px-0 py-4 md:px-6">
                    <div className="flex items-center">
                
                      <div className="font-medium text-gray-800">
                        {document.titre}
                      </div>
                    </div>
                  </td> 
                  <td className="px-3 py-4 md:px-6">{document.origine}</td>
                  <td className="px-3 py-4 md:px-6">{document.datePub.split('-').join('/')}</td>
                  <td className="px-3 py-4 md:px-6">{document.objet.substring(0, document.objet.indexOf('-')).trim()}</td>
                  <td className="px-3 py-4 md:px-6">
                    <span>
                      <Link className="inline-flex items-center px-1 py-1 text-sm font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800" 
                      to={`/pdf/${document.objet}`}> Afficher
                        <svg aria-hidden="true" className="w-4 h-4 ml-2 -mr-1" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clipRule="evenodd"></path></svg>
                      </Link>
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {numRows < filteredDocuments.length && (
          <div className="flex justify-center my-4">
            {currentPage > 1 && (
              <button
                className="bg-gray-200 hover:bg-gray-300 text-gray-700 font-semibold px-4 py-2 mr-2 rounded"
                onClick={() => setCurrentPage(currentPage - 1)}
              >
                Previous
              </button>
            )}
            {currentPage < totalPages && (
              <button
                className="bg-gray-200 hover:bg-gray-300 text-gray-700 font-semibold px-4 py-2 ml-2 rounded"
                onClick={() => setCurrentPage(currentPage + 1)}
              >
                Next
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default ResponsiveTable;
