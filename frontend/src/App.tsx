import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Landing from "./pages/landing/Landing";
import Home from "./pages/home/Home";
import Remixes from "./pages/remixes/Remixes";

const App = () => {
  return (
    <Router>
      <div>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/landing" element={<Landing />} />
          <Route path="/remixes" element={<Remixes />} />
        </Routes>
      </div>
    </Router>
  );
};

export default App;
