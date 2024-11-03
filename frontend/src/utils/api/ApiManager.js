module.exports = () => {
    const URL = `${process.env.REACT_APP_API_URL}`;

    return {
        search: async (query) => {
            return await fetch(`${URL}/api/search?query=${query}`)
                .then(response => response.json())
                .catch(error => console.error('Error:', error));;
        },
    }
}