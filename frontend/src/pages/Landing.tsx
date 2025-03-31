import {useState, useEffect} from 'react'; 

// main landing page
export default function Landing() {
    const [playlistId, setPlaylistId] = useState(""); 
    const [playlist, setPlaylist] = useState<any[]>([]); 
    const [playlists, setPlaylists] = useState<any[]>([]); 
    const [currentIndex, setCurrentIndex] = useState(0);
    
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
            <div>Section 3: Content for the third section</div>
        ]; // List of sections to display
    
        return (
            <div>
                {sections[currentIndex]} {/* Display the current section */}
                <div>
                    <button onClick={handleBack} disabled={currentIndex === 0}>
                        Back
                    </button>
                </div>
            </div>
        );
    };

    const getUserPlaylists = async () => {
        const response = await fetch("http://localhost:8080/api/playlists/all"); 
        const text = await response.text(); 
        setPlaylists(JSON.parse(text)); 
    }

    const getPlaylist = async (playListId:string) => {
        const response = await fetch(`http://localhost:8080/api/playlist?id=${playListId}`, {
            method : "GET", 
        });0
        const text = await response.text(); 
        setPlaylist(JSON.parse(text)); 
    }

    useEffect(() => {
        getUserPlaylists(); 
    }, []); 

    useEffect(() => {
        getPlaylist(playlistId); 
    }, [playlistId])

    return (
        <div>
            <Stepper />
        </div>
    )
}