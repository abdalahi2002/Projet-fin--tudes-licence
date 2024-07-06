import React, { useState, useEffect, useRef } from 'react';
import { RiArrowDropDownLine, RiArrowDropUpLine } from 'react-icons/ri';
import { useNavigate } from 'react-router-dom';

const Navbar = ({ onObjectChange }) => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [objects, setObjects] = useState([]);
  const [selectedObject, setSelectedObject] = useState('');
  const navigate = useNavigate();
  const dropdownRef = useRef(null);
  const listRef = useRef(null);
  const timeoutRef = useRef(null);

  useEffect(() => {
    fetchObjects();
  }, []);

  useEffect(() => {
    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  const fetchObjects = async () => {
    try {
      const response = await fetch('http://localhost:9001/api/metadata');
      const data = await response.json();
      setObjects([...new Set(data.map((document) => document.objet.substring(0, document.objet.indexOf('-')).trim()))]);
    } catch (error) {
      console.error('Erreur lors de la récupération des objets :', error);
    }
  };

  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  const handleObjectChange = (objet) => {
    setSelectedObject(objet);
    onObjectChange(objet); // Appel de la fonction onObjectChange avec l'objet sélectionné
    navigate(`/footer?filter=${objet}`);
    setIsDropdownOpen(false);
  };

  const handleClickOutside = (event) => {
    if (
      dropdownRef.current &&
      !dropdownRef.current.contains(event.target) &&
      listRef.current && // Vérifier si listRef.current n'est pas null
      !listRef.current.contains(event.target)
    ) {
      setIsDropdownOpen(false);
    }
  };

  const handleMenuHover = () => {
    clearTimeout(timeoutRef.current);
    setIsDropdownOpen(true);
  };

  const handleMenuLeave = () => {
    clearTimeout(timeoutRef.current);
    timeoutRef.current = setTimeout(() => {
      setIsDropdownOpen(false);
    }, 200); // Délai de 200 ms avant de masquer la liste
  };

  const filteredObjects = objects.filter((objet) =>
  objet.toLowerCase().includes("appel") || objet.toLowerCase().includes("attribution")
);


  return (
    <nav className="navbar bg-white shadow bg-white w-full px-8 md:px-auto fixed top-0 z-50">
      <div className="md:h-16 h-28 mx-auto md:px-4 container flex items-center justify-between flex-wrap md:flex-nowrap top-0">
        <div className="text-indigo-500 md:order-1">
          <div className="shadow-gray-300">
            <img src="logo.png" alt="Logo" className="h-16 w-30" />
          </div>
        </div>

        <div className="text-gray-500 order-3 w-full md:w-auto md:order-2">
          <ul className="flex font-semibold justify-between ml-16">
            <li className="md:px-4 md:py-2 text-indigo-500">
              <div
                className="relative inline-block"
                ref={dropdownRef}
                onMouseEnter={handleMenuHover}
                onMouseLeave={handleMenuLeave}
              >
                <button
                  id="dropdownNavbarLink"
                  data-dropdown-toggle="dropdownNavbar"
                  className="text-gray-700 hover:bg-gray-50 border-b border-gray-100 md:hover:bg-transparent md:border-0 pl-3 pr-4 py-2 md:hover:text-blue-700 md:p-0 font-medium flex items-center justify-between w-full md:w-auto"
                  onClick={toggleDropdown}
                >
                  Annonces{' '}
                  {isDropdownOpen ? (
                    <RiArrowDropUpLine className="w-4 h-4 ml-1" />
                  ) : (
                    <RiArrowDropDownLine className="w-4 h-4 ml-1" />
                  )}
                </button>
                {isDropdownOpen && (
                  <div
                    id="dropdownNavbar"
                    className="bg-white text-base z-10 divide-y divide-gray-100 rounded shadow my-4 w-44 absolute max-h-56 overflow-y-auto"
                    ref={listRef}
                    style={{
                      maxHeight: 'unset',
                      overflowY: 'auto',
                      scrollbarWidth: 'none',
                      msOverflowStyle: 'none',
                      '&::-webkit-scrollbar': { display: 'none' },
                    }}
                  >
                    {filteredObjects.map((objet, index) => (
                      <button
                        key={index}
                        className="w-full py-2 px-4 text-left hover:bg-gray-100"
                        onClick={() => handleObjectChange(objet)}
                      >
                        {objet}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            </li>
            <li className="md:px-4 md:py-2 hover:text-indigo-400 ml-24">
              <a href="/">Accueil</a>
            </li>
            <li className="md:px-4 md:py-2 hover:text-indigo-400 ml-20">
              <a href="/search">Search</a>
            </li>

            <li className="md:px-4 md:py-2 hover:text-indigo-400 ml-24">
              <a href="/archive">archive</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
