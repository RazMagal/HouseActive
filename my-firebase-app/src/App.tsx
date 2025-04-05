import { useState } from 'react'
import './App.css'
import SignInButton from './components/SignInButton'
import { auth } from './assets/config/firebase'
import { useAuthState } from 'react-firebase-hooks/auth'

function App() {
  const [count, setCount] = useState(0)
  const [user] = useAuthState(auth)

  return (
    <>      
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>
          count is {count}
        </button>
        <p>
          Lolz it's a counter from the template I used to start this project
        </p>
      </div>
      
      <div>
        <h1>Welcome to HouseActive</h1>
        {!user &&  (
        <SignInButton />
        )}
      </div>
      {user && (
      <div>
        <h1><p> {auth.currentUser?.displayName} </p></h1>
        <img src={auth.currentUser?.photoURL || ""} width="100" height="100" alt="Profile" />
        <p> {auth.currentUser?.email} </p>
        <p> your user uid is: {auth.currentUser?.uid} </p>
        <button onClick={() => auth.signOut()}>Sign Out</button>
      </div>
      )}
    </>
  )
}

export default App 
