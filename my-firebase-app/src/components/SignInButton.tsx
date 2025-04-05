import React from "react";
import { auth, provider } from "../assets/config/firebase";
import { signInWithPopup } from "firebase/auth";

const SignInButton: React.FC = () => {
  const handleSignIn = async () => {
    const result = await signInWithPopup(auth, provider);
    const user = result.user;
    console.log("User signed in:", user);
}

  return (
    <button onClick={handleSignIn} style={{ padding: "10px", fontSize: "16px" }}>
      Sign In with Google
    </button>
  );
};

export default SignInButton;