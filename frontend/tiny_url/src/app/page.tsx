"use client";

import Link from "next/link";
import React from "react";

const HomePage: React.FC = () => {
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
        backgroundColor: "#f7f7f7",
      }}
    >
      <h1
        style={{
          fontSize: "2rem",
          color: "#333",
          marginBottom: "2rem",
          fontWeight: "bold",
        }}
      >
        TinyURL Shortener
      </h1>
      <div style={{ display: "flex", gap: "1rem" }}>
        <Link href="/register">
          <button
            style={{
              padding: "0.5rem 1rem",
              backgroundColor: "#4CAF50",
              color: "#fff",
              border: "none",
              borderRadius: "4px",
              fontSize: "16px",
              cursor: "pointer",
            }}
          >
            Register
          </button>
        </Link>
        <Link href="/login">
          <button
            style={{
              padding: "0.5rem 1rem",
              backgroundColor: "#0070f3",
              color: "#fff",
              border: "none",
              borderRadius: "4px",
              fontSize: "16px",
              cursor: "pointer",
            }}
          >
            Log In
          </button>
        </Link>
      </div>
    </div>
  );
};

export default HomePage;
