import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const AdvancedSearch = ({ onFilterChange }) => {
  const [origines, setOrigines] = useState([]);
  const [objets, setObjets] = useState([]);
  const [dates, setDates] = useState([]);
  const [selectedOrigine, setSelectedOrigine] = useState('');
  const [selectedObjet, setSelectedObjet] = useState('');
  const [selectedDate, setSelectedDate] = useState('');
  const [documents, setDocuments] = useState([]);

  useEffect(() => {
    fetchDocuments();
  }, []);

  const navigate = useNavigate();

  const fetchDocuments = async () => {
    const response = await fetch('http://localhost:9001/api/metadata');
    const data = await response.json();
    setDocuments(data);
    setOrigines([...new Set(data.map((document) => document.origine))]);
    setObjets([...new Set(data.map((document) => document.objet.substring(0, document.objet.indexOf('-')).trim()))]);
    setDates([...new Set(data.map((document) => document.datePub))]);
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    // Filtrer les documents en fonction des critères sélectionnés
    const filteredDocuments = documents.filter(
      (document) =>
        document.origine.toLowerCase().includes(selectedOrigine.toLowerCase()) &&
        document.objet.toLowerCase().includes(selectedObjet.toLowerCase()) &&
        document.datePub.toLowerCase().includes(selectedDate.toLowerCase())
    );

    // Appeler la fonction de rappel pour passer les critères de filtrage
    onFilterChange({
      origine: selectedOrigine,
      objet: selectedObjet,
      date: selectedDate,
    });

    // Rediriger vers le tableau avec les critères de filtrage dans le chemin
    navigate('/footer');
  };


  return (
    <div className="min-h-screen p-6 bg-gray-100 flex items-center justify-center">
      <div className="container max-w-screen-lg mx-auto">
        <div>
          <h2 className="font-semibold text-xl text-gray-600">Recherche avancée</h2>
          <p className="text-gray-500 mb-6">Vous pouvez affiner et personnaliser vos résultats de recherche pour trouver exactement ce que vous recherchez.</p>
          <div className="bg-white rounded shadow-lg p-4 px-4 md:p-8 mb-6">
            <div className="grid gap-4 gap-y-2 text-sm grid-cols-1 lg:grid-cols-3">
              <div className="text-gray-600">
                <p className="font-medium text-lg">Personnaliser votre recherche</p>
              </div>
              <div className="lg:col-span-2">
                <div className="grid gap-4 gap-y-2 text-sm grid-cols-1 md:grid-cols-5">
                  <div className="md:col-span-5">
                    <label htmlFor="keyword">Mot Clé</label>
                    <input type="text" name="keyword" id="keyword" className="h-10 border mt-1 rounded px-4 w-full bg-gray-50" 
                     value={selectedObjet || selectedOrigine || selectedDate}
                     onChange={(e) => setSelectedObjet(e.target.value)}/>
                  </div>
                  <div className="md:col-span-5">
                    <label htmlFor="origin">Origine</label>
                    <select
                      name="origin"
                      id="origin"
                      className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                      value={selectedOrigine}
                      onChange={(e) => setSelectedOrigine(e.target.value)}
                    >
                      <option value="">Toutes les origines</option>
                      {origines.map((origine) => (
                        <option key={origine} value={origine}>
                          {origine}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="md:col-span-5">
                    <label htmlFor="subject">Objet</label>
                    <select
                      name="subject"
                      id="subject"
                      className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                      value={selectedObjet}
                      onChange={(e) => setSelectedObjet(e.target.value)}
                    >
                      <option value="">Tous les objets</option>
                      {objets.map((objet) => (
                        <option key={objet} value={objet}>
                          {objet}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="md:col-span-5">
                    <label htmlFor="datePub">Date</label>
                    <select
                      name="datePub"
                      id="datePub"
                      className="h-10 border mt-1 rounded px-4 w-full bg-gray-50"
                      value={selectedDate}
                      onChange={(e) => setSelectedDate(e.target.value)}
                    >
                      <option value="">Toutes les dates</option>
                      {dates.map((date) => (
                        <option key={date} value={date}>
                          {date}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="md:col-span-5 text-right">
                    <div className="inline-flex items-end">
                      <button onClick={handleSubmit}
                      type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" on>Lancer la Recherche</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdvancedSearch;
