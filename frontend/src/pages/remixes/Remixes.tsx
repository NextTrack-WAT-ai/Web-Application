import { useLocation } from 'react-router-dom';
import './Remixes.css'; 
import { useEffect, useState } from 'react';
import { SpotifyPlaylist } from '../../models/spotify-playlist';
import { NextTrack } from '../../models/next-track';

export default function Remixes() {
    const location = useLocation(); 
    const playlists = location.state.playlists; // pass this down from previous page
    const [loading, setLoading] = useState(true); 
    const [remixes, setRemixes] = useState<string[]>([]);  
    const [remix, setRemix] = useState<SpotifyPlaylist>(); 
    const [remixTracks, setRemixTracks] = useState<NextTrack[]>([]); 

    const getRemixes = async () => {
        setLoading(true); 
        const res = await fetch("http://localhost:8080/user/remixes"); 
        const data = await res.json(); 
        setRemixes(data); 
        setLoading(false); 
    }

    const getRemix = async (id: string) => {
        setLoading(true);
        const res = await fetch(`http://localhost:8080/api/playlist?id=${id}`);
        const data = await res.json();
        setRemixTracks(data || []);
        setRemix(playlists.filter((x:any) => x.id === id)[0]);
        setLoading(false);
      };

    useEffect(() => {
        getRemixes(); 
    }, [])
}