import { useState, useEffect } from "react";
import "./Landing.css";
import { UserProfile } from "../../models/user-profile";
import { useNavigate } from "react-router-dom";
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
  const [isReordered, setIsReordered] = useState(false);
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
    // check sesison storage for editing playlist id
    const editingPlaylist = sessionStorage.getItem("editingPlaylist");
    if (editingPlaylist) {
      setPlaylist(JSON.parse(editingPlaylist));
      setPlaylistId(JSON.parse(editingPlaylist).id);
      getPlaylist(JSON.parse(editingPlaylist).id);
      setStep("detail");
      sessionStorage.removeItem("editingPlaylist");
    }
  }, []);

  useEffect(() => {
    if (email) {
      attemptRegister(email);
    }
  }, [email]);

  useEffect(() => {
    if (playlistId && step === "detail") getPlaylist(playlistId);
    if (playlistId && step === "remixed") getShuffledPlaylist(playlistTracks);
  }, [playlistId, step]);

  const getUserEmail = async () => {
    const res = await fetch(`${apiUrl}/user/email`);
    const text = await res.text();
    setEmail(text);
    sessionStorage.setItem("email", text);
  };

  const attemptRegister = async (email: string) => {
    await fetch(`${apiUrl}/user/create/${email}`, {
      method: "POST",
    });
  };

  const getUserProfile = async () => {
    const res = await fetch(`${apiUrl}/user/profile`);
    const text = await res.text();
    setUserProfile(JSON.parse(text));
  };

  const getUserPlaylists = async () => {
    setLoading(true);
    const res = await fetch(`${apiUrl}/api/playlists/all`);
    const data = await res.json();
    setPlaylists(data || []);
    console.log(data);
    setLoading(false);
  };

  const getPlaylist = async (id: string) => {
    setLoading(true);
    const res = await fetch(`${apiUrl}/api/playlist/${id}`);
    const data = await res.json();
    setPlaylistTracks(data || []);
    setLoading(false);
  };

  const getShuffledPlaylist = async (tracks: NextTrack[]) => {
    setLoading(true);
    try {
      const payload = {
        email: email,
        tracks: tracks,
      };
      const res = await fetch(`${apiUrl}/api/playlist/reshuffle`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        throw new Error("Failed to fetch reshuffled playlist");
      }

      const data = await res.json();
      const indexedData = data.map((track: NextTrack, index: number) => ({
        ...track,
        trackIndex: index,
      }));
      setShuffled(indexedData);
    } catch (error) {
      console.error("Error reshuffling playlist:", error);
    } finally {
      setLoading(false);
    }
  };

  const addRemixToAccount = async (email: string, playlistId: string) => {
    const response = await fetch("/remixes/add", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, playlistId }),
    });
    if (response.ok) {
      console.log("Remix added successfully!");
    } else {
      console.log("Error adding remix.");
    }
  };

  const saveReshuffledPlaylist = async () => {
    const payload = {
      userId: userProfile?.id,
      email: email,
      playlist: shuffled,
    };

    const res = await fetch(`${apiUrl}/api/playlist/save`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const data = await res.text();
    console.log(data);
    setShuffledPlaylistLink(data || "");
  };

  const handleReorder = (reorderedTracks: NextTrack[]) => {
    setShuffled(reorderedTracks);
    setIsReordered(true);
  };

  // ------------------ COMPONENTS ------------------

  const Logo = () => (
    <div className="logo">
      <div className="logo-inner">NextTrack</div>
    </div>
  );

  // if your ide throws an error here, please pay it no attention, this works just fine
  const UserFooter = () => (
    <div className="user-footer">
      <div className="user-avatar">
        <img
          className="user-avatar"
          src={userProfile?.images[0]?.url}
          alt="profile"
        />
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
            if (step === "remixed") {
              setStep("detail");
              setShuffledPlaylistLink("");
            }
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
                    setPlaylist(p);
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
          <SongTable
            tracks={playlistTracks}
            onReorder={handleReorder}
            isDraggable={false}
            isRemix={false}
          />
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

            <div
              className="view-button"
              onClick={() => setShowOriginal(!showOriginal)}
              style={{ margin: "1rem 0", width: "fit-content" }}
            >
              {showOriginal
                ? "Hide Original Playlist"
                : "Show Original Playlist"}
            </div>

            <div
              style={{
                display: "flex",
                flexDirection: "row",
                gap: "2rem",
                width: "100%",
                height: "50vh",
              }}
            >
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  gap: "1rem",
                  width: showOriginal ? "50%" : "100%",
                }}
              >
                <h3>Remixed</h3>
                <SongTable
                  tracks={shuffled}
                  onReorder={handleReorder}
                  isDraggable={true}
                  isRemix={true}
                />
              </div>

              {showOriginal && (
                <div
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: "1rem",
                    width: "50%",
                  }}
                >
                  <h3>Original</h3>
                  <SongTable
                    tracks={playlistTracks}
                    onReorder={handleReorder}
                    isDraggable={false}
                    isRemix={false}
                  />
                </div>
              )}
            </div>
            <Stepper />
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
  };

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
