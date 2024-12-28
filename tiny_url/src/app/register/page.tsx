"use client";

import React, { useState} from "react";
import Link from "next/link"; // Use Link for navigation in the App Router

const RegistrationPage: React.FC = () => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        try{
            const response = await fetch("http://localhost:8080/api/users/register", {
                method: "POST",
                headers: {
                "Content-Type": "application/json",
                },
                body: JSON.stringify({ username, email, password }),
            });

            if (response.ok){
                setSuccess("Registration successful! You can now log in.");
                setUsername("");
                setEmail("");
                setPassword("");

            } else {
                const errorMessage = await response.text();
                setError(errorMessage);
            }
        } catch (err){
            setError("An error occurred. Please try again later.");
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
            onSubmit={handleRegister}
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
            <h1 style={{ textAlign: "center", marginBottom: "20px" }}>Register</h1>
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
                  color: "#333",
                  fontSize: "16px",
                }}
              />
            </div>
            <div style={{ marginBottom: "1rem" }}>
              <label htmlFor="email" style={{ fontWeight: "bold" }}>
                Email
              </label>
              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter your email"
                required
                style={{
                  width: "100%",
                  padding: "0.5rem",
                  marginTop: "0.5rem",
                  border: "1px solid #ccc",
                  borderRadius: "4px",
                  color: "#333",
                  fontSize: "16px",
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
                  color: "#333",
                  fontSize: "16px",
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
              Register
            </button>
            <Link href="/..">
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
                Login
              </button>
            </Link>
          </form>
        </div>
      );
    };

    export default RegistrationPage;