// App.js
import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import { createTheme, ThemeProvider, CssBaseline } from '@mui/material';

import Results from './pages/Results';
import Home from './pages/Home';
import { setCookie, getCookie } from './utils/cookies/Cookies';
import About from './pages/About';

// Define light and dark themes
const lightTheme = createTheme({
	breakpoints: {
		values: {
			xs: 0,   // Extra small devices (default)
			sm: 640, // Change `sm` to 640px
			md: 900, // Medium devices (default)
			lg: 1200, // Large devices (default)
			xl: 1536, // Extra large devices (default)
		},
	},
	palette: {
		primary: {
			main: '#e5e7eb',
		},
		secondary: {
			main: '#9ca3af',
		},
		background: {
			default: '#ffffff',
		},
	},
	components: {
		MuiOutlinedInput: {
			styleOverrides: {
				root: {
					borderRadius: 9999
				}

			},
		},
	}
});

const darkTheme = createTheme({
	breakpoints: {
		values: {
			xs: 0,   // Extra small devices (default)
			sm: 640, // Change `sm` to 640px
			md: 900, // Medium devices (default)
			lg: 1200, // Large devices (default)
			xl: 1536, // Extra large devices (default)
		},
	},
	palette: {
		primary: {
			main: '#333333',
		},
		secondary: {
			main: '#555555',
		},
		background: {
			default: '#121212',
		},
	},
	components: {
		MuiOutlinedInput: {
			styleOverrides: {
				notchedOutline: {
					borderColor: '#333333', // Default border color
				},
				focused: {
					borderColor: '#333333', // Border color when focused
				},
				root: {
					'&:hover .MuiOutlinedInput-notchedOutline': {
						borderColor: '#444444', // Border color when hovered
					},
					borderRadius: 9999
				}

			},
		},
		MuiInputBase: {
			styleOverrides: {
				root: {
					color: '#ffffff', // Text color
					'& input': {
						color: '#ffffff', // Ensure text color matches
					},
				},
			},
		},
	},
});

function App() {
	const [theme, setTheme] = useState(lightTheme);

	useEffect(() => {
		// Check for the theme cookie and set the theme accordingly
		const savedTheme = getCookie('theme');
		if (savedTheme === 'dark') {
			setTheme(darkTheme);
		} else {
			setTheme(lightTheme);
		}
	}, []);

	let themeString = theme === lightTheme ? 'light' : 'dark';

	// Function to toggle theme and save it in cookies
	const toggleTheme = () => {
		const newTheme = theme === lightTheme ? darkTheme : lightTheme;
		setTheme(newTheme);
		themeString = newTheme === lightTheme ? 'light' : 'dark';
		setCookie('theme', themeString, 7);
	};

	return (
		<ThemeProvider theme={theme}>
			<CssBaseline />
			<Router>
				<Routes>
					<Route path="/" element={<Home toggleTheme={toggleTheme} selectedTheme={themeString} />} />
					<Route path="/search" element={<Results toggleTheme={toggleTheme} selectedTheme={themeString} />} />
					<Route path='/about' element={<About toggleTheme={toggleTheme} selectedTheme={themeString} />} />
					<Route path="*" element={<Navigate to="/" />} />
				</Routes>
			</Router>
		</ThemeProvider>
	);
}

export default App;