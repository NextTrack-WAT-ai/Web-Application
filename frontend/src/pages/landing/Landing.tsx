import { useState, useEffect } from "react";
import "./Landing.css";
import { UserProfile } from "../../models/user-profile";
import { SpotifyPlaylist } from "../../models/spotify-playlist";
import SongTable from "../../components/SongTable";
import { NextTrack } from "../../models/next-track";
import Navbar from "../../components/Navbar";

export default function Landing() {
  const apiUrl = import.meta.env.VITE_API_URL as string;
  const [playlistId, setPlaylistId] = useState("");
  const [userProfile, setUserProfile] = useState<UserProfile>();
  const [playlistTracks, setPlaylistTracks] = useState<NextTrack[]>([]);
  const [playlist, setPlaylist] = useState<SpotifyPlaylist>();
  const [shuffled, setShuffled] = useState<NextTrack[]>([]);
  const [playlists, setPlaylists] = useState<SpotifyPlaylist[]>([]);
  const [shuffledPlaylistLink, setShuffledPlaylistLink] = useState("");
  const [loading, setLoading] = useState(true);
  const [step, setStep] = useState("select");
  const [authToken, setAuthToken] = useState<string | null>(null);

  useEffect(() => {
    // Extract auth token from URL (set by OAuth callback) and persist it
    const params = new URLSearchParams(window.location.search);
    const tokenFromUrl = params.get("auth");
    if (tokenFromUrl) {
      sessionStorage.setItem("authToken", tokenFromUrl);
      window.history.replaceState({}, document.title, window.location.pathname);
    }
    const token = tokenFromUrl ?? sessionStorage.getItem("authToken");
    setAuthToken(token);
  }, []);

  useEffect(() => {
    if (!authToken) return;
    getUserProfile();
    getUserPlaylists();
    attemptRegister();

    const editingPlaylist = sessionStorage.getItem("editingPlaylist");
    if (editingPlaylist) {
      const p = JSON.parse(editingPlaylist);
      setPlaylist(p);
      setPlaylistId(p.id);
      getPlaylist(p.id);
      setStep("detail");
      sessionStorage.removeItem("editingPlaylist");
    }
  }, [authToken]);

  useEffect(() => {
    if (!authToken) return;
    if (playlistId && step === "detail") getPlaylist(playlistId);
    if (playlistId && step === "remixed") getShuffledPlaylist(playlistTracks);
  }, [playlistId, step]);

  const authHeaders = () => ({
    "Content-Type": "application/json",
    "X-Auth-Token": authToken ?? "",
  });

  const attemptRegister = async () => {
    await fetch(`${apiUrl}/user/create`, {
      method: "POST",
      headers: authHeaders(),
    });
  };

  const getUserProfile = async () => {
    const res = await fetch(`${apiUrl}/user/profile`, { headers: authHeaders() });
    if (!res.ok) return;
    setUserProfile(await res.json());
  };

  const getUserPlaylists = async () => {
    setLoading(true);
    const res = await fetch(`${apiUrl}/api/playlists/all`, { headers: authHeaders() });
    if (!res.ok) { setLoading(false); return; }
    setPlaylists((await res.json()) ?? []);
    setLoading(false);
  };

  const getPlaylist = async (id: string) => {
    setLoading(true);
    const res = await fetch(`${apiUrl}/api/playlist/${id}`, { headers: authHeaders() });
    if (!res.ok) { setLoading(false); return; }
    setPlaylistTracks((await res.json()) ?? []);
    setLoading(false);
  };

  const getShuffledPlaylist = async (tracks: NextTrack[]) => {
    setLoading(true);
    try {
      const res = await fetch(`${apiUrl}/api/playlist/reshuffle`, {
        method: "POST",
        headers: authHeaders(),
        body: JSON.stringify({ tracks }),
      });
      if (!res.ok) throw new Error("Failed to fetch reshuffled playlist");
      const data = await res.json();
      setShuffled(data.map((track: NextTrack, index: number) => ({ ...track, trackIndex: index })));
    } catch (error) {
      console.error("Error reshuffling playlist:", error);
    } finally {
      setLoading(false);
    }
  };

  const saveReshuffledPlaylist = async () => {
    const res = await fetch(`${apiUrl}/api/playlist/save`, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify({ playlist: shuffled }),
    });
    if (!res.ok) return;
    const url = await res.text();
    setShuffledPlaylistLink(url ?? "");
  };

  const handleReorder = (reorderedTracks: NextTrack[]) => {
    setShuffled(reorderedTracks);
  };

  const Logo = () => (
    <div className="logo">
      <div className="logo-inner">NextTrack</div>
    </div>
  );

  const UserFooter = () => (
    <div className="user-footer">
      <div className="user-avatar">
        <img className="user-avatar" src={userProfile?.images[0]?.url} alt="profile" />
      </div>
      Logged in: {userProfile?.displayName}
    </div>
  );

  const Stepper = () => (
    <div style={{ width: "100%", alignSelf: "flex-start", marginTop: "1rem" }}>
      {step !== "select" && (
        <button
          className="view-button"
          onClick={() => {
            if (step === "remixed") { setStep("detail"); setShuffledPlaylistLink(""); }
            if (step === "detail") setStep("select");
          }}
        >
          ← Back
        </button>
      )}
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
              <img src={p.images?.[0]?.url || "/api/placeholder/50/50"} alt="Playlist" className="playlist-img" />
              <div className="playlist-info">
                <div className="playlist-name">{p.name}</div>
                <button className="view-button" onClick={() => { setPlaylistId(p.id); setPlaylist(p); setStep("detail"); }}>
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
            <img src={playlist?.images?.[0]?.url || "/api/placeholder/50/50"} alt="Playlist" className="playlist-img" />
            <div className="playlist-details">
              <div>{playlistTracks.length} songs</div>
              <div className="playlist-name">{playlist?.name}</div>
              <button onClick={() => setStep("remixed")}>Remix Playlist</button>
            </div>
          </div>
          <SongTable tracks={playlistTracks} onReorder={handleReorder} isDraggable={false} isRemix={false} />
          <Stepper />
        </>
      )}
    </div>
  );

  const RemixedPlaylistView = () => {
    const [showOriginal, setShowOriginal] = useState(true);

    return (
      <div className="page playlist-detail">
        <Logo />
        <h2 className="section-title">Remixed Playlist</h2>
        {loading ? (
          <div>Loading remixed playlist...</div>
        ) : (
          <>
            <div className="highlighted-playlist">
              <img src={playlist?.images?.[0]?.url || "/api/placeholder/50/50"} alt="Playlist" className="playlist-img" />
              <div className="playlist-details">
                <div>{shuffled.length} songs</div>
                <div className="playlist-name">Remix of {playlist?.name}</div>
                <button onClick={saveReshuffledPlaylist}>Add Playlist To Account</button>
              </div>
            </div>

            <div className="view-button" onClick={() => setShowOriginal(!showOriginal)}
              style={{ margin: "1rem 0", width: "fit-content" }}>
              {showOriginal ? "Hide Original Playlist" : "Show Original Playlist"}
            </div>

            <div style={{ display: "flex", flexDirection: "row", gap: "2rem", width: "100%", height: "50vh" }}>
              <div style={{ display: "flex", flexDirection: "column", gap: "1rem", width: showOriginal ? "50%" : "100%" }}>
                <h3>Remixed</h3>
                <SongTable tracks={shuffled} onReorder={handleReorder} isDraggable={true} isRemix={true} />
              </div>
              {showOriginal && (
                <div style={{ display: "flex", flexDirection: "column", gap: "1rem", width: "50%" }}>
                  <h3>Original</h3>
                  <SongTable tracks={playlistTracks} onReorder={handleReorder} isDraggable={false} isRemix={false} />
                </div>
              )}
            </div>
            <Stepper />
            {shuffledPlaylistLink && (
              <div className="view-playlist-button" onClick={() => window.open(shuffledPlaylistLink, "_blank")}>
                Listen To Remixed Playlist
              </div>
            )}
          </>
        )}
      </div>
    );
  };

  if (!authToken) {
    return (
      <div>
        <Navbar />
        <div style={{ padding: "2rem", textAlign: "center" }}>
          Session not found. <a href="/">Please log in again.</a>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      {step === "select" && <PlaylistSelectionView />}
      {step === "detail" && <PlaylistDetailView />}
      {step === "remixed" && <RemixedPlaylistView />}
      <UserFooter />
    </div>
  );
}
