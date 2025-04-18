import "./Home.css";

export default function Home() {
  const SpotifyLogin = async () => {
    const response = await fetch("http://localhost:8080/api/login");
    const text = await response.text();
    window.location.replace(text);
  };

  return (
    <div className="container">
      <h1 className="logo">NextTrack</h1>

      <div className="tagline-container">
        <h2 className="tagline">
          Reshuffle Your Playlist in the Optimal Order
        </h2>
      </div>

      <button onClick={SpotifyLogin} className="remix-button">
        Remix a Playlist
      </button>
    </div>
  );
}
