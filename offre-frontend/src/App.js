import React, { useState } from 'react';
import './App.css';
import Footer from './Components/Footer/footer';
import Document from './Components/DocumentView/Document';
import Table from './Components/Tables/Table';
import Navbar from './Components/Navbar/Navbar';
import Search from './Components/searchDiv/Search';
import Cards from './Components/archive/Cards';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

function App() {
  const [filter, setFilter] = useState({ origine: '', objet: '', date: '' , keyword: ''});

  const handleFilterChange = (filter) => {
    setFilter(filter);
  };
  const [selectedObject, setSelectedObject] = useState('');

  const handleObjectChange = (objet) => {
    setSelectedObject(objet);
  };

  return (
    <Router>
      <div>
        <Navbar onObjectChange={handleObjectChange} />
        <div className="mt-20"></div> {/* Ajout de la marge supérieure */}
        <Routes>
          <Route path="/" element={<Table />} />
          <Route path="/pdf/:objet" element={<Document />} />
          <Route
            path="/footer"
            element={<Footer filter={filter} selectedObject={selectedObject} />
            }
          />
         <Route
            path="/Search"
            element={<Search onFilterChange={handleFilterChange} />}
          />
          <Route path="/archive" element={<Cards />} />
        </Routes>
      </div>
    </Router>
   
  );
}

export default App;
