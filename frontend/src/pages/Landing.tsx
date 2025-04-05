import {useState, useEffect} from 'react'; 

// main landing page
export default function Landing() {
    const [playlistId, setPlaylistId] = useState(""); 
    const [playlist, setPlaylist] = useState<any[]>([]); 
    const [shuffled, setShuffled] = useState<any[]>([]); // array to store the shuffled playlist
    const [playlists, setPlaylists] = useState<any[]>([]); 
    const [currentIndex, setCurrentIndex] = useState(0);
    const [loading, setLoading] = useState(true); 
    
    const Stepper = () => {
        const handleBack = () => {
            if (currentIndex > 0) {
                setCurrentIndex(currentIndex - 1);
            }
        };

        const handleNext = () => {
            if (currentIndex < 3) {
                setCurrentIndex(currentIndex + 1);
            }
        };

        const sections = [
            <div>
                {playlists ? 
                <ul>
                    {playlists.map((playlist) => {
                        return (
                            <li key={playlist.id}>
                                <button onClick={() => {
                                    handleNext(); 
                                    setPlaylistId(playlist.id); 
                                }} disabled={currentIndex === 2}>
                                    {playlist.name}
                                </button>
                            </li>
                        )
                    })}
                </ul> 
                : <h2>Loading...</h2>}
            </div>,
            <div>
                {playlist ?
                    <ul>
                        {playlist.map((track) => {
                            return (
                                <li key={track.track.id}>
                                    {track.track.name} by {track.track.artists[0].name}
                                </li>
                            )
                        })}
                    </ul>
                : <h2>Loading...</h2>}
                <button onClick={() => {
                    handleNext(); 
                    // TODO add in api call to AI shuffle endpoint
                }}>Shuffle With NextTrack</button>
            </div>,
            <div>
                <button>Export To Spotify</button>
            </div>
        ]; // List of sections to display
    
        return (
            <div>
                {sections[currentIndex]} {/* Display the current section */}
                <div>
                    <button onClick={handleBack} disabled={currentIndex === 0}>
                        back
                    </button>
                </div>
            </div>
        );
    };

    const getUserPlaylists = async () => {
        setLoading(true); 
        const response = await fetch("http://localhost:8080/api/playlists/all"); 
        const text = await response.text(); 
        setPlaylists(JSON.parse(text));
        setLoading(false);  
    }

    const getPlaylist = async (playListId:string) => {
        setLoading(true); 
        const response = await fetch(`http://localhost:8080/api/playlist?id=${playListId}`, {
            method : "GET", 
        });
        const text = await response.text(); 
        setPlaylist(JSON.parse(text)); 
        setLoading(false); 
    }

    const getShuffledPlaylist = async (playlistId:string) => {
        setLoading(true); 
        const response = await fetch(`http://localhost:8080/api/playlist/reshuffle?id=${playlistId}`); 
        const text = await response.text(); 
        setLoading(false); 
    }

    useEffect(() => {
        getUserPlaylists(); 
    }, []); 

    useEffect(() => {
        getPlaylist(playlistId); 
    }, [playlistId])

    return (
        <div>
            {loading ? <h2>Loading...</h2> : <Stepper />}
        </div>
    )
}