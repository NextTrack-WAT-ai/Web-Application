export default function Home() {
    const SpotifyLogin = async () => {
        const response = await fetch("http://localhost:8080/api/login")
        const text = await response.text(); 
        window.location.replace(text); 
    }
    
    return (
        <div>
            <h2>Homepage</h2>
            <button onClick={SpotifyLogin}>Log In With Spotify</button>
        </div>
    )
}