import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import SearchService from '../services/SearchService';

const SearchResults = () => {
    const [results, setResults] = useState([]);
    const location = useLocation();
    const q = new URLSearchParams(location.search).get("q");

    useEffect(() => {
        if (q) {
            SearchService.search(q).then(res => {
                setResults(res.data.content);
            });
        }
    }, [q]);

    return (
        <div className="container">
            <h2>"{q}" için arama sonuçları</h2>
            <div className="product-grid">
                {results.length > 0 ? (
                    results.map(p => <div key={p.id}>{p.title} - {p.brand}</div>)
                ) : (
                    <p>Sonuç bulunamadı.</p>
                )}
            </div>
        </div>
    );
};
export default SearchResults;