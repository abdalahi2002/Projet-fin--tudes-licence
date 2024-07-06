import React, { useState, useEffect } from 'react';
import { BiTimeFive } from 'react-icons/bi';
import { useLocation, useNavigate } from 'react-router-dom';

const Card = ({ filter }) => {
  const [documents, setDocuments] = useState([]);
  const [filteredDocuments, setFilteredDocuments] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const location = useLocation();
  const navigate = useNavigate();

  const queryParams = new URLSearchParams(location.search);
  const filterObjet = queryParams.get('filter');

  useEffect(() => {
    fetchDocuments();
  }, [currentPage, filterObjet, filter]);

  const fetchDocuments = async () => {
    try {
      const response = await fetch('http://localhost:9001/api/metadata');
      const data = await response.json();

      let filteredDocuments = data;

      if (filterObjet) {
        filteredDocuments = data.filter((document) => document.objet.toLowerCase().includes(filterObjet.toLowerCase()));
      } else {
        if (filter.origine) {
          filteredDocuments = filteredDocuments.filter((document) => document.origine.toLowerCase().includes(filter.origine.toLowerCase()));
        }
        if (filter.objet) {
          filteredDocuments = filteredDocuments.filter((document) => document.objet.toLowerCase().includes(filter.objet.toLowerCase()));
        }
        if (filter.date) {
          filteredDocuments = filteredDocuments.filter((document) => document.datePub.toLowerCase().includes(filter.date.toLowerCase()));
        }
      }

      const itemsPerPage = 10;
      const startIndex = (currentPage - 1) * itemsPerPage;
      const endIndex = startIndex + itemsPerPage;
      const paginatedDocuments = filteredDocuments.slice(startIndex, endIndex);

      setDocuments(paginatedDocuments);
      setFilteredDocuments(filteredDocuments);
      setTotalPages(Math.ceil(filteredDocuments.length / itemsPerPage));
    } catch (error) {
      console.error('Erreur lors de la récupération des documents :', error);
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages) {
      const nextPage = currentPage + 1;
      setCurrentPage(nextPage);
      navigate(`/footer?page=${nextPage}&filter=${filterObjet}`);
    }
  };

  const handlePreviousPage = () => {
    if (currentPage > 1) {
      const previousPage = currentPage - 1;
      setCurrentPage(previousPage);
      navigate(`/footer?page=${previousPage}&filter=${filterObjet}`);
    }
  };
  return (
    <div className="max-w-4xl px-16 my-8 py-10 bg-white border border-black rounded-lg shadow-md mx-auto">
    <div className="bg-indigo-900 rounded-t-lg px-12 py-6">
      <h1 className="text-white text-2xl font-bold text-center">
       Resultat des {filterObjet || filter.date || filter.objet || filter.origine}
      </h1>
    </div>

    {documents.map((document) => (
      <div key={document.objet} className="max-w-4xl px-10 my-8 py-6 bg-white border border-black rounded-lg shadow-md mx-auto">
        <div className="mt-2">
          <a
            className="text-2xl text-gray-700 font-bold hover:text-gray-600"
            href="#"
          >
            {document.objet.substring(0, document.objet.indexOf('-')).trim()}
          </a>
          <hr className="my-3 w-16 border-gray-300" />
          <h2 className="text-gray-700 font-bold">{document.origine}</h2>
          <hr className="my-5 w-16 border-gray-300 bg-indigo-900" />
          <p className="mt-2 text-gray-600">{document.titre}</p>
        </div>
        <div className="flex justify-between items-center mt-4">
          <a
            className="px-2 py-1 bg-indigo-900 text-gray-100 font-bold rounded hover:bg-gray-500"
            href={`/pdf/${document.objet}`}
          >
            En savoir plus
          </a>
          <div>
            <a className="flex-row items-center" href="#">
              <h1 className="text-gray-700 font-bold">
                <BiTimeFive /> {document.datePub.split('-').reverse().join(' - ')}
              </h1>
            </a>
          </div>
        </div>
      </div>
      ))}

      <nav className="flex justify-center mt-8">
        <button
          className="px-4 py-2 mx-1 bg-indigo-900 text-white font-bold rounded hover:bg-gray-500"
          onClick={handlePreviousPage}
          disabled={currentPage === 1}
        >
          Précédent
        </button>
        <button
          className="px-4 py-2 mx-1 bg-indigo-900 text-white font-bold rounded hover:bg-gray-500"
          onClick={handleNextPage}
          disabled={currentPage === totalPages}
        >
          Suivant
        </button>
      </nav>
    </div>
  );
};

export default Card;
