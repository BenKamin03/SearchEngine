import { useTheme } from "@emotion/react";
import {
    AppBar,
    IconButton,
    InputAdornment,
    TextField,
    Toolbar,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import { CiDark, CiLight } from "react-icons/ci";
import { IoIosClose, IoIosSearch } from "react-icons/io";
import { Link } from "react-router-dom";
import CustomSnackbar from "../snackbar/CustomSnackbar";
import { MdErrorOutline } from "react-icons/md";

const Navbar = ({ selectedTheme, toggleTheme, defaultSearchValue }) => {
    const theme = useTheme();

    const [search, setSearch] = useState(defaultSearchValue);
    const [error, setError] = useState("");
    const [isError, setIsError] = useState(false);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (search) {
            if (search !== defaultSearchValue) {
                window.location.href = `/search?q=${encodeURIComponent(
                    search
                )}`;
            }
        } else {
            setError("Please enter a search term.");
            setIsError(true);
        }
    };

    useEffect(() => {
        console.log(error);
    }, [error]);

    return (
        <>
            <AppBar
                position="sticky"
                elevation={1}
                sx={{ backgroundColor: theme.palette.background.default, boxShadow: `0px 1px 3px ${selectedTheme == 'light' ? "#0000003f" : '#ffffff3f' }` }}
            >
                <Toolbar className="md:space-x-16 space-x-4 relative flex w-full justify-between">
                    <div className="md:space-x-16 space-x-4 flex w-full justify-start items-center">
                        <Link to="/">
                            <img
                                src="/logo.png"
                                alt="logo"
                                className="h-8 aspect-auto object-contain"
                            />
                        </Link>

                        <form
                            onSubmit={handleSubmit}
                            className="w-full md:w-1/3"
                        >
                            <TextField
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                                placeholder="Search..."
                                size="small"
                                fullWidth
                                InputProps={{
                                    startAdornment: (
                                        <InputAdornment position="start">
                                            <IoIosSearch
                                                style={{
                                                    color:
                                                        selectedTheme === "dark"
                                                            ? "white"
                                                            : "black",
                                                }}
                                            />
                                        </InputAdornment>
                                    ),
                                    endAdornment: search ? (
                                        <InputAdornment position="end">
                                            <IconButton
                                                onClick={() => setSearch("")}
                                                sx={{
                                                    color:
                                                        selectedTheme ===
                                                        "light"
                                                            ? "black"
                                                            : "white",
                                                }}
                                            >
                                                <IoIosClose />
                                            </IconButton>
                                        </InputAdornment>
                                    ) : null,
                                }}
                            />
                        </form>
                    </div>
                    <IconButton
                        onClick={toggleTheme}
                        sx={{
                            color: selectedTheme == "light" ? "black" : "white",
                        }}
                    >
                        {selectedTheme === "dark" ? <CiLight /> : <CiDark />}
                    </IconButton>
                </Toolbar>
            </AppBar>

            <CustomSnackbar
                open={isError}
                onClose={() => setIsError(false)}
                sx={{ border: "1px solid red" }}
                selectedTheme={selectedTheme}
            >
                <div className="flex justify-center items-center space-x-2">
                    <MdErrorOutline size={"1.5rem"} color="red" />
                    <p className="snackbar-title">{error}</p>
                </div>
            </CustomSnackbar>
        </>
    );
};

export default Navbar;
