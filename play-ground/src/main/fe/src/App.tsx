import {useState} from 'react'
import './App.css'

import {PrimeReactProvider} from 'primereact/api';
import sampleRequest from "./SampleRequest.ts";

function App() {
    const [count, setCount] = useState(0)
    const [output, setOutput] = useState({})

    return (
        <PrimeReactProvider>
            <div className="card">
                <button onClick={() => {
                    setCount((count) => count + 1);
                    sampleRequest().then((response) => {
                        setOutput(response.data);
                    });
                }}>
                    count is {count}
                </button>
                <div>{ JSON.stringify(output) }</div>
            </div>
        </PrimeReactProvider>
    )
}

export default App
