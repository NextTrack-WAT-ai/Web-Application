import { useNavigate } from "react-router-dom";

const Navbar = () => {
  const navigate = useNavigate();

  return (
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
          navigate("/remixes");
        }}
      >
        My Remixes
      </div>
    </div>
  );
};

export default Navbar;
