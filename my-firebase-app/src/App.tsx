import { useState, useEffect } from 'react'
import './App.css'
import SignInButton from './components/SignInButton'
import { auth } from './assets/config/firebase'
import { useAuthState } from 'react-firebase-hooks/auth'
import { db } from './assets/config/firebase';
import { collection, doc, getDocs } from 'firebase/firestore';

function App() {
  const [count, setCount] = useState(0)
  const [user] = useAuthState(auth)
  const [tasks, setTasks] = useState<any[]>([]);

  useEffect(() => {
    const fetchTasks = async () => {
      if (auth.currentUser) {
        const userId = auth.currentUser.uid;
        const usersRef = collection(db, 'users');
        const userDocRef = doc(usersRef, userId);
        const tasksRef = collection(userDocRef, 'tasks');
        // Fetch the tasks from the users' unique document
        const tasksSnapshot = await getDocs(tasksRef);
        console.log("Tasks Snapshot:", tasksSnapshot);
        // Map the tasks to an array of objects
        const tasksList = tasksSnapshot.docs.map((doc) => ({ id: doc.id, ...doc.data() }));
        console.log("Tasks List:", tasksList);
        setTasks(tasksList);
      }
    };

    fetchTasks();
  }, [auth.currentUser]);
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

        <h2>Your Tasks</h2>
        <ul>
          {tasks.map((task) => (
            <li key={task.id}>{task.name || 'Unnamed Task'}</li>
            ))}
            </ul>
      </div>
      )}
    </>
  )
}

export default App 
