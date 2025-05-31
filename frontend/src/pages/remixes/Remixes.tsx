import "./Remixes.css";
import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";
import { SpotifyPlaylist } from "../../models/spotify-playlist";
import { useNavigate } from "react-router-dom";
export default function Remixes() {
  const [loading, setLoading] = useState(true);
  const [remixes, setRemixes] = useState<SpotifyPlaylist[]>([]);

  useEffect(() => {
    getRemixes();
  }, []);

  const getRemixes = async () => {
    setLoading(true);
    const email = sessionStorage.getItem("email");
    const res = await fetch(`http://localhost:8080/api/remixes/${email}`);
    const data = await res.json();
    setRemixes(data);
    console.log(data);
    setLoading(false);
  };

  const navigate = useNavigate();

  return (
    <div>
      <Navbar />
      <div className="remixes-container">
        {loading ? (
          <div>Loading...</div>
        ) : (
          <>
            <h2 className="section-title">Remixes</h2>
            <div
              className="remix-list"
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(2, 1fr)",
                gap: "20px",
              }}
            >
              {remixes.map((p: SpotifyPlaylist, index) => (
                <div className="playlist-item" key={p.id || index}>
                  <img
                    src={p.images?.[0]?.url || "/api/placeholder/50/50"}
                    alt="Playlist"
                    className="playlist-img"
                  />
                  <div className="playlist-info">
                    <div className="playlist-name">{p.name}</div>
                    <div className="playlist-buttons">
                      <button
                        className="view-button"
                        onClick={() => {
                          window.open(
                            p.externalUrls.externalUrls.spotify,
                            "_blank"
                          );
                        }}
                      >
                        View on Spotify
                      </button>
                      <button
                        className="edit-button"
                        onClick={() => {
                          sessionStorage.setItem(
                            "editingPlaylist",
                            JSON.stringify(p)
                          );
                          navigate("/landing");
                        }}
                      >
                        Edit
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
      </div>
    </div>
  );
}
