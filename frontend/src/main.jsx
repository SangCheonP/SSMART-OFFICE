import { createRoot } from "react-dom/client";
import App from "./App.jsx";
import ReactModal from "react-modal";

ReactModal.setAppElement("#root");

createRoot(document.getElementById("root")).render(<App />);
