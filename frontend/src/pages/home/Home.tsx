import "./Home.css";

export default function Home() {
  const apiUrl = import.meta.env.VITE_API_URL as string;
  const SpotifyLogin = async () => {
    const response = await fetch(`${apiUrl}/api/login`);
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
