import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Cards = () => {
  const [years, setYears] = useState([]);
  const [selectedYear, setSelectedYear] = useState('');
  const [results, setResults] = useState({});
  const navigate = useNavigate();

  useEffect(() => {
    fetchYears();
  }, []);

  const fetchYears = async () => {
    try {
      const response = await fetch('http://localhost:9001/api/metadata');
      const data = await response.json();

      const yearCounts = {};
      data.forEach((document) => {
        const year = document.datePub.split('-')[0];
        if (yearCounts.hasOwnProperty(year)) {
          yearCounts[year] += 1;
        } else {
          yearCounts[year] = 1;
        }
      });

      setResults(yearCounts);

      const uniqueYears = Object.keys(yearCounts);
      setYears(uniqueYears);
    } catch (error) {
      console.error('Erreur lors de la récupération des années :', error);
    }
  };

  const handleYearSelection = (year) => {
    setSelectedYear(year);
    navigate(`/footer?filter=${year}`);
  };

  const groupBy = (array, groupSize) => {
    const groups = [];
    let group = [];

    array.forEach((item, index) => {
      group.push(item);
      if ((index + 1) % groupSize === 0 || index === array.length - 1) {
        groups.push(group);
        group = [];
      }
    });

    return groups;
  };

  const groupedYears = groupBy(years, 4);

  return (
    <div className=" min-h-screen flex items-center justify-end pr-4">
    <div className="grid grid-cols-4 gap-4">
      {groupedYears.map((group, index) => (
        <div key={index} className="flex flex-col gap-4">
          {group.map((year) => (
            <div
              key={year}
              className="flex flex-col items-center justify-center h-full bg-black rounded-3xl text-white"
              style={{ background: '#1B1BC9' }}
            >
              <div className="px-6 py-8 sm:p-10 sm:pb-6 text-center">
                <h2 className="text-lg font-medium tracking-tighter text-white lg:text-3xl">
                  Archive
                </h2>
                
                <h6 className="mt-6 text-3xl font-light tracking-tight">
                  {year} ({results[year] || 0})
                </h6>
              </div>
              <div className="px-6 pb-8 sm:px-8">
                <a
                  href={`/footer?filter=${year}`}
                  className="w-full px-6 py-2.5 text-center bg-white border-2 border-white rounded-full inline-flex items-center justify-center text-black text-sm hover:bg-transparent hover:border-white hover:text-white focus:outline-none focus-visible:outline-white focus-visible:ring-white"
                  aria-describedby="tier-starter"
                >
                  En savoir Plus
                </a>
              </div>
            </div>
          ))}
          {group.length < 4 && (
            <div className="bg-white rounded-lg shadow-md p-6 opacity-0"></div>
          )}
        </div>
      ))}
    </div>
  </div>
);
};

export default Cards;
