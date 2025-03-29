import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Landing from './pages/Landing'; 
import Home from './pages/Home';

const App = () => {
  return (
    <Router>
      <div>
        <nav>
          <ul>
            <li><Link to="/">Home</Link></li>
          </ul>
        </nav>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/landing" element={<Landing />} />
        </Routes>
      </div>
    </Router>
  );
};

export default App;