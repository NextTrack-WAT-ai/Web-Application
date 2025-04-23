import { useState, useEffect } from "react";
import "./Landing.css";
import { UserProfile } from "../../models/user-profile";
import { useNavigate } from "react-router-dom";
import { SpotifyPlaylist } from "../../models/spotify-playlist";
import SongTable from "../../components/SongTable";
import { NextTrack } from "../../models/next-track";

export default function Landing() {
  const [playlistId, setPlaylistId] = useState("");
  const [userProfile, setUserProfile] = useState<UserProfile>();
  const [playlistTracks, setPlaylistTracks] = useState<NextTrack[]>([]);
  const [playlist, setPlaylist] = useState<SpotifyPlaylist>();
  const [shuffled, setShuffled] = useState([]);
  const [playlists, setPlaylists] = useState<SpotifyPlaylist[]>([]);
  const [shuffledPlaylistLink, setShuffledPlaylistLink] = useState("");
  const [loading, setLoading] = useState(true);
  const [step, setStep] = useState("select"); // "select", "detail", "remixed"
  const navigate = useNavigate();

  const [email, setEmail] = useState(""); 

  useEffect(() => {
    getUserProfile();
    getUserPlaylists();
    getUserEmail(); 
  }, []);

  useEffect(() => {
    if (email) {attemptRegister(email);}
  }, [email])

  useEffect(() => {
    if (playlistId && step === "detail") getPlaylist(playlistId);
    if (playlistId && step === "remixed") getShuffledPlaylist(playlistTracks);
  }, [playlistId, step]);

  const getUserEmail = async () => {
    const res = await fetch("http://localhost:8080/user/email"); 
    const text = await res.text(); 
    setEmail(text);
  }

  const attemptRegister = async (email:string) => {
    await fetch(`http://localhost:8080/user/create?email=${email}`, {
      method : 'POST'
    });
  }

  const getUserProfile = async () => {
    const res = await fetch("http://localhost:8080/user/profile");
    const text = await res.text();
    setUserProfile(JSON.parse(text));
  };

  const getUserPlaylists = async () => {
    setLoading(true);
    const res = await fetch("http://localhost:8080/api/playlists/all");
    const data = await res.json();
    setPlaylists(data || []);
    console.log(data);
    setLoading(false);
  };

  const getPlaylist = async (id: string) => {
    setLoading(true);
    const res = await fetch(`http://localhost:8080/api/playlist?id=${id}`);
    const data = await res.json();
    setPlaylistTracks(data || []);
    setPlaylist(playlists.filter((x) => x.id === id)[0]);
    setLoading(false);
  };

  const getShuffledPlaylist = async (tracks: NextTrack[]) => {
    setLoading(true);
    try {
      const res = await fetch("http://localhost:8080/api/playlist/reshuffle", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(tracks),
      });

      if (!res.ok) {
        throw new Error("Failed to fetch reshuffled playlist");
      }

      const data = await res.json();
      setShuffled(data || []);
    } catch (error) {
      console.error("Error reshuffling playlist:", error);
    } finally {
      setLoading(false);
    }
  };

  const addRemixToAccount = async (email:string, playlistId:string) => {
    await fetch(`http://localhost:8080/user/remixes/add?email=${email}&playlistid=${playlistId}`, {
      method : "PATCH"
    })
  }

  const saveReshuffledPlaylist = async () => {
    const payload = {
      userId: userProfile?.id,
      playlist: shuffled,
    };

    const res = await fetch("http://localhost:8080/api/playlist/save", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const data = await res.text();
    console.log(data);
    setShuffledPlaylistLink(data || "");

    const match = data.match(/\/([^\/?#]+)(?:[?#]|$)/g);
    const playlistId = match ? match[match.length - 1].replace(/\//g, '') : null;
    if (!playlistId) {
      console.error("Something went wrong, playlistId is null"); 
    } else {
      addRemixToAccount(email, playlistId); 
    }
  };

  // ------------------ COMPONENTS ------------------

  const Header = () => (
    <div className="header">
      <div
        className="logo-text"
        onClick={() => {
          navigate("/");
        }}
      >
        NextTrack
      </div>
      <div 
      className="header-right"
      onClick={() => {
        navigate("/remixes", { // we pass these down to avoid redundant api calls
          state: {
            playlists : playlists, 
            user : userProfile
          }
        }); 
      }}>
        My Remixes
      </div>
    </div>
  );

  const Logo = () => (
    <div className="logo">
      <div className="logo-inner">NextTrack</div>
    </div>
  );

  // if your ide throws an error here, please pay it no attention, this works just fine
  const UserFooter = () => (
    <div className="user-footer">
      <div className="user-avatar">
        <img className="user-avatar" src={userProfile?.images[0]?.url} alt="profile" /> 
      </div>
      Logged in: {userProfile?.displayName}
    </div>
  );
  
  const PlaylistSelectionView = () => (
    <div className="page playlist-selection">
      <Logo />
      <h2 className="section-title">Choose a Playlist</h2>

      {loading ? (
        <div>Loading playlists...</div>
      ) : (
        <div className="playlist-grid">
          {playlists.map((p: SpotifyPlaylist, index) => (
            <div className="playlist-item" key={p.id || index}>
              <img
                src={p.images?.[0]?.url || "/api/placeholder/50/50"}
                alt="Playlist"
                className="playlist-img"
              />
              <div className="playlist-info">
                <div className="playlist-name">{p.name}</div>
                <button
                  className="view-button"
                  onClick={() => {
                    setPlaylistId(p.id);
                    setStep("detail");
                  }}
                >
                  View
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );

  const PlaylistDetailView = () => (
    <div className="page playlist-detail">
      <Logo />
      <h2 className="section-title">Viewing Playlist</h2>

      {loading ? (
        <div>Loading playlist...</div>
      ) : (
        <>
          <div className="highlighted-playlist">
            <img
              src={playlist?.images?.[0]?.url || "/api/placeholder/50/50"}
              alt="Playlist"
              className="playlist-img"
            />
            <div className="playlist-details">
              <div>{playlistTracks.length} songs</div>
              <div className="playlist-name">{playlist?.name}</div>
              <button onClick={() => setStep("remixed")}>Remix Playlist</button>
            </div>
          </div>

          <SongTable tracks={playlistTracks} />
        </>
      )}
    </div>
  );

  const RemixedPlaylistView = () => (
    <div className="page playlist-detail">
      <Logo />
      <h2 className="section-title">Remixed Playlist</h2>

      {loading ? (
        <div>Loading remixed playlist...</div>
      ) : (
        <>
          <div className="highlighted-playlist">
            <img
              src={playlist?.images?.[0]?.url || "/api/placeholder/50/50"}
              alt="Playlist"
              className="playlist-img"
            />
            <div className="playlist-details">
              <div>{shuffled.length} songs</div>
              <div className="playlist-name">Remix of {playlist?.name}</div>
              <button onClick={saveReshuffledPlaylist}>
                Add Playlist To Account
              </button>
            </div>
          </div>

          <SongTable tracks={shuffled} />
          {shuffledPlaylistLink && (
            <div
              className="view-playlist-button"
              onClick={() => window.open(shuffledPlaylistLink, "_blank")}
            >
              Listen To Remixed Playlist
            </div>
          )}
        </>
      )}
    </div>
  );

  return (
    <div>
      <Header />
      {step === "select" && <PlaylistSelectionView />}
      {step === "detail" && <PlaylistDetailView />}
      {step === "remixed" && <RemixedPlaylistView />}
      <UserFooter />
    </div>
  );
}
