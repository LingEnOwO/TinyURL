"use client";

import React, { useState } from 'react';
import Link from "next/link";

const LoginPage: React.FC = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        try{
            const response = await fetch("http://localhost:8080/api/users/login", {
             method: "POST",
             headers: {
               "Content-Type": "application/json",
             },
             body: JSON.stringify({ username, password }),
           });

            if (response.ok) {
                const message = await response.text();
                setSuccess(message);
                console.log("Login successful", message);
                // Redirect to dashboard or homepage on success
                // router.push("/dashboard");
            } else{
                const errorMessage = await response.text();
                setError(errorMessage);
                console.log("Login failed: ", errorMessage);
            }
        } catch (err){
            setError("An error occurred. Please try again later.");
            console.error(err);
        }
    };

    return (
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            height: "100vh",
            backgroundColor: "#f7f7f7",
          }}
        >
          <form
            onSubmit={handleLogin}
            style={{
              maxWidth: "400px",
              width: "100%",
              padding: "20px",
              backgroundColor: "#fff",
              border: "1px solid #ccc",
              borderRadius: "8px",
              boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.1)",
            }}
          >
            <h1 style={{ textAlign: "center", marginBottom: "20px" }}>Login</h1>
            {error && (
              <div
                style={{
                  color: "red",
                  marginBottom: "1rem",
                  textAlign: "center",
                }}
              >
                {error}
              </div>
            )}
            {success && (
              <div
                style={{
                  color: "green",
                  marginBottom: "1rem",
                  textAlign: "center",
                }}
              >
                {success}
              </div>
            )}
            <div style={{ marginBottom: "1rem" }}>
              <label htmlFor="username" style={{ fontWeight: "bold" }}>
                Username
              </label>
              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter your username"
                required
                style={{
                  width: "100%",
                  padding: "0.5rem",
                  marginTop: "0.5rem",
                  border: "1px solid #ccc",
                  borderRadius: "4px",
                  fontSize: "16px",
                  color: "#333",
                  backgroundColor: "#fff",
                }}
              />
            </div>
            <div style={{ marginBottom: "1rem" }}>
              <label htmlFor="password" style={{ fontWeight: "bold" }}>
                Password
              </label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                required
                style={{
                  width: "100%",
                  padding: "0.5rem",
                  marginTop: "0.5rem",
                  border: "1px solid #ccc",
                  borderRadius: "4px",
                  fontSize: "16px",
                  color: "#333",
                  backgroundColor: "#fff",
                }}
              />
            </div>
            <button
              type="submit"
              style={{
                padding: "0.5rem 1rem",
                backgroundColor: "#0070f3",
                color: "#fff",
                border: "none",
                borderRadius: "4px",
                fontSize: "16px",
                cursor: "pointer",
                marginBottom: "1rem",
              }}
            >
              Login
            </button>
            <Link href="/register">
              <button
                type="button"
                style={{
                  padding: "0.5rem 1rem",
                  backgroundColor: "#4CAF50",
                  color: "#fff",
                  border: "none",
                  borderRadius: "4px",
                  fontSize: "16px",
                  cursor: "pointer",
                  width: "100%",
                }}
              >
                Register
              </button>
            </Link>
          </form>
        </div>
      );
    };


export default LoginPage;