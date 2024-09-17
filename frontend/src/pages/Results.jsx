import { useTheme } from "@emotion/react";
import {
    AppBar,
    IconButton,
    InputAdornment,
    LinearProgress,
    Pagination,
    TextField,
    Toolbar,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import ApiManager from "../utils/api/ApiManager";
import { CiDark, CiLight } from "react-icons/ci";
import { IoIosClose } from "react-icons/io";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import Navbar from "../components/navbar/Navbar";

const getQueryStringObject = (queryString) => {
    const params = new URLSearchParams(queryString);
    const result = {};
    for (const [key, value] of params.entries()) {
        result[key] = value;
    }
    return result;
};

const pageSize = 10;

const Results = ({ selectedTheme, toggleTheme }) => {
    const location = useLocation();
    const navigate = useNavigate();
    const API = ApiManager();

    const queryString = getQueryStringObject(window.location.search);

    if (!queryString.q) {
        window.location.href = "/";
    }

    const pageNum = parseInt(queryString.page) ? parseInt(queryString.page) : 1;

    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(() => {
        if (!loading && page * pageSize > results.length) {
            const searchParams = new URLSearchParams(location.search);

            searchParams.set("page", Math.trunc(results.length / pageSize));

            navigate({
                pathname: location.pathname,
                search: searchParams.toString(),
            });

            return Math.trunc(results.length / pageSize);
        }
        return pageNum;
    });

    useEffect(() => {
        setLoading(true);
        API.search(queryString.q).then((data) => {
            setResults(data);
            setLoading(false);

            if (page * pageSize > data.length) {
                const searchParams = new URLSearchParams(location.search);
    
                searchParams.set("page", Math.trunc(data.length / pageSize));
    
                navigate({
                    pathname: location.pathname,
                    search: searchParams.toString(),
                });
    
                setPage(Math.trunc(data.length / pageSize));
            }
            
        });
    }, []);

    function formatURL(url) {
        return url
            .split("/")
            .pop()
            .split(".html")[0]
            .replace(/([a-z])([A-Z])/g, "$1 $2") // Space before uppercase letters
            .replace(/(\d+)/g, " $1") // Space before numbers
            .replace(/-/g, " ") // Replace hyphens with spaces
            .replace(/^./, (char) => char.toUpperCase()); // Capitalize the first letter
    }

    return (
        <div>
            <Navbar
                selectedTheme={selectedTheme}
                toggleTheme={toggleTheme}
                defaultSearchValue={queryString.q}
            />
            {loading && <LinearProgress />}
            {results && (
                <List>
                    {results
                        .slice((page - 1) * pageSize, page * pageSize)
                        .map((result, index) => (
                            <>
                                <ListItem key={index}>
                                    <a
                                        href={result.where}
                                        rel="noreferrer"
                                        className={`flex flex-col w-full h-full text-nowrap ${
                                            selectedTheme == "light"
                                                ? "bg-gray-100 text-black hover:bg-gray-200"
                                                : "bg-neutral-800 text-white hover:bg-neutral-700"
                                        } p-4 rounded-lg`}
                                    >
                                        <h1 className="text-xl truncate">
                                            {formatURL(result.where)}
                                        </h1>
                                        <div className="flex space-x-2">
                                            <p className="text-sm ml-2">
                                                {(result.score * 100).toFixed(
                                                    1
                                                )}
                                                %
                                            </p>
                                            <p className="italic text-sm ml-4 truncate">
                                                {result.where}
                                            </p>
                                        </div>
                                    </a>
                                </ListItem>
                                {index < results.length - 1 && (
                                    <hr className="my-2 mx-12" />
                                )}
                            </>
                        ))}
                </List>
            )}
            <div className="w-full flex-shrink flex justify-center items-center">
                <Pagination
                    count={Math.trunc(results.length / pageSize)}
                    page={page}
                    onChange={(e, v) => {
                        setPage(v);

                        const searchParams = new URLSearchParams(
                            location.search
                        );

                        // Set the 'page' query parameter to 1
                        if (v == 1) {
                            searchParams.delete("page");
                        } else {
                            searchParams.set("page", v);
                        }

                        // Navigate to the new URL with updated query parameters
                        navigate({
                            pathname: location.pathname,
                            search: searchParams.toString(),
                        });

                        window.scrollTo({ top: 0, behavior: "smooth" });
                    }}
                    showFirstButton
                    showLastButton
                />
            </div>
        </div>
    );
};

export default Results;
