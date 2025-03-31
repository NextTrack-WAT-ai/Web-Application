import Stepper from "./Stepper";
import {useState} from 'react'; 

export default function Landing() {
    const [playlistId, setPlaylistId] = useState(""); 

    const getUserArtists = async () => {
        const response = await fetch("http://localhost:8080/api/user-top-artists"); 
        const text = await response.text(); 
        console.log(text); 
    }

    const getUserPlaylists = async () => {
        const response = await fetch("http://localhost:8080/api/playlists/all"); 
        const text = await response.text(); 
        console.log(JSON.parse(text)); // gives us an array of playlists
    }

    const getPlaylist = async (playListId:string) => {
        const response = await fetch(`http://localhost:8080/api/playlist?id=${playListId}`, {
            method : "GET", 
        });0
        const text = await response.text(); 
        console.log(JSON.parse(text));  
    }

    const handleIdChange = (e:any) => {
        e.preventDefault(); 
        setPlaylistId(e.target.value); 
    }

    return (
        <div>
            <button onClick={getUserArtists}>Get top user artists</button>
            <button onClick={getUserPlaylists}>Get top user playlists</button>
            <input type="text" value = {playlistId} onChange={handleIdChange}></input>
            <button onClick={() => getPlaylist(playlistId)}>Get Playlist</button>
            <Stepper />
        </div>
    )
}