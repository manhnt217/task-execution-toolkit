import './App.css'
import Pallet from "./component/pallet/Pallet.tsx";
import Control from "./component/control/Control.tsx";
import Diagram from "./component/diagram/Diagram.tsx";

function App() {
  return (
    <div className="w-full h-full flex">
      <div className="surface-800 w-1">
        <Pallet/>
      </div>
      <div className="flex-grow-1 flex flex-column">
        <div className="flex-grow-1">
          <Diagram />
        </div>
        <div className="surface-900 w-full h-15rem">
          <Control />
        </div>
      </div>
    </div>
  )
}

export default App
