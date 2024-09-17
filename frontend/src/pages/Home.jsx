import { Button, IconButton, InputAdornment, TextField } from "@mui/material";
import { IoIosSearch, IoMdInformationCircleOutline } from "react-icons/io";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom"; // Import useNavigate
import ApiManager from "../utils/api/ApiManager";
import { CiLight } from "react-icons/ci";
import { CiDark } from "react-icons/ci";
import { useTheme } from "@emotion/react";
import CustomSnackbar from "../components/snackbar/CustomSnackbar";
import { MdErrorOutline } from "react-icons/md";

const Home = ({ selectedTheme, toggleTheme }) => {
    const theme = useTheme();
    const navigate = useNavigate();
    const API = ApiManager();

    const [search, setSearch] = useState("");
    const [error, setError] = useState("");
    const [isError, setIsError] = useState(false);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (search) {
            navigate(`/search?q=${encodeURIComponent(search)}`);
        } else {
            setError("Please enter a search term.");
            setIsError(true);
        }
    };

    console.log(selectedTheme);

    return (
        <form
            onSubmit={handleSubmit}
            className="w-screen h-screen flex justify-center items-center flex-col space-y-8 relative"
            style={{ color: selectedTheme == "light" ? "black" : "white" }}
        >
            <IconButton
                onClick={toggleTheme}
                sx={{
                    color: selectedTheme == "light" ? "black" : "white",
                    position: "absolute",
                    top: "1rem",
                    right: "1rem",
                }}
            >
                {selectedTheme === "dark" ? <CiLight /> : <CiDark />}
            </IconButton>
            <img
                src="/logo.png"
                alt="logo"
                className="max-w-[33%] max-h-[33%]"
            />
            <div className="w-3/4">
                <TextField
                    fullWidth
                    placeholder="Search..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <IoIosSearch
                                    style={{
                                        color:
                                            selectedTheme == "dark" && "white",
                                    }}
                                />
                            </InputAdornment>
                        ),
                    }}
                    sx={{
                        borderRadius: "50px", // fully round the border
                        "& .MuiOutlinedInput-root": {
                            borderRadius: "50px", // input field border
                        },
                    }}
                />
            </div>
            <div className="flex space-x-4">
                <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    sx={{
                        textTransform: "none",
                        fontWeight: "normal",
                        border: "1px solid transparent",
                        "&:hover": {
                            border: "1px solid black",
                            backgroundColor: "primary.main",
                        },
                    }}
                >
                    Kamin Search
                </Button>
                <Button
                    variant="contained"
                    color="primary"
                    sx={{
                        textTransform: "none",
                        fontWeight: "normal",
                        border: "1px solid transparent",
                        "&:hover": {
                            border: "1px solid black",
                            backgroundColor: "primary.main",
                        },
                    }}
                    onClick={async () => {
                        if (search) {
                            const results = await API.search(search);
                            if (results.length > 0) {
                                window.location.href = results[0].where;
                            }
                        }
                    }}
                >
                    I'm Feeling Lucky
                </Button>
            </div>
            <IconButton
                onClick={() => navigate("/about")}
                sx={{
                    color: selectedTheme == "light" ? "black" : "white",
                    position: "absolute",
                    bottom: "1rem",
                    left: "1rem",
                }}
            >
                <IoMdInformationCircleOutline size={"1.5rem"} />
            </IconButton>
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
        </form>
    );
};

export default Home;
