import Stepper from "./Stepper";

export default function Landing() {
    const getUserArtists = async () => {
        const response = await fetch("http://localhost:8080/api/user-top-artists"); 
        const text = await response.text(); 
        console.log(text); 
    }

    const getUserPlaylists = async () => {
        const response = await fetch("http://localhost:8080/api/all-user-playlists"); 
        const text = await response.text(); 
        console.log(JSON.parse(text)); // gives us an array of playlists
    }

    return (
        <div>
            <button onClick={getUserArtists}>Get top user artists</button>
            <button onClick={getUserPlaylists}>Get top user playlists</button>
            <Stepper />
        </div>
    )
}