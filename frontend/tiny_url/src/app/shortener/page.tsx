"use client";

import React, {useState, useEffect} from "react";

const ShortenerPage: React.FC = () => {
    const [longUrl, setLongUrl] = useState("");
    const [alias, setAlias] = useState("");
    const [shortUrl, setShortUrl] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) {
            setError("You are not logged in. Redirecting to login...");
            setTimeout(() => {
                window.location.href = "/login";
            }, 2000);
        }
      }, []);

    const handleShortener = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setShortUrl(null);

        const token = localStorage.getItem("token");
        //console.log("Token:", token);

        if (!token) {
            setError("Authentication error. Please log in.");
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/api/url", {
                method: "POST",
                headers: {
                  "Authorization": `Bearer ${token}`,
                  "Content-Type": "application/json",
                },
                body: JSON.stringify({ longUrl, alias: alias || undefined }),
            });

            if (response.ok) {
                const data = await response.json();
                if (data && data.shortUrl) {
                    setShortUrl(data.shortUrl);
                }
                else {
                    setError("Unexpected response from the server. Please try again.");
                }
            }
            else {
                const text = await response.text(); // Read response as plain text
                let errorMessage = "An error occurred";
                try {
                    const errorData = JSON.parse(text); // Try to parse as JSON
                    if (errorData && errorData.error) {
                        errorMessage = errorData.error; // Use server-provided error message
                    }
                } catch {
                    console.error("Error parsing server response as JSON:", text);
                }
                setError(errorMessage);

            }
        } catch (err) {
            console.error("Network error:", err); // Log network errors
            setError("An error occurred. Please try again later.");
        }
    };

    return (
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            height: "100vh",
            padding: "20px",
            backgroundColor: "#f7f7f7",
          }}
        >
          <h1 style={{ fontSize: "2rem", marginBottom: "1.5rem", color: "#333" }}>
            Shorten a URL
          </h1>
          <form
            onSubmit={handleShortener}
            style={{
              width: "100%",
              maxWidth: "500px",
              padding: "20px",
              backgroundColor: "#fff",
              border: "1px solid #ccc",
              borderRadius: "8px",
              boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
            }}
          >
            <div style={{ marginBottom: "1rem" }}>
              <label htmlFor="longUrl" style={{ fontWeight: "bold", color: "#333" }}>
                Long URL
              </label>
              <input
                id="longUrl"
                type="text"
                value={longUrl}
                onChange={(e) => setLongUrl(e.target.value)}
                placeholder="Enter the long URL"
                required
                style={{
                  width: "100%",
                  padding: "0.5rem",
                  marginTop: "0.5rem",
                  border: "1px solid #ccc",
                  borderRadius: "4px",
                  fontSize: "16px",
                  color: "#333"
                }}
              />
            </div>
            <div style={{ marginBottom: "1rem" }}>
              <label htmlFor="alias" style={{ fontWeight: "bold", color: "#333" }}>
                Custom Alias (Optional)
              </label>
              <input
                id="alias"
                type="text"
                value={alias}
                onChange={(e) => setAlias(e.target.value)}
                placeholder="Enter a custom alias"
                style={{
                  width: "100%",
                  padding: "0.5rem",
                  marginTop: "0.5rem",
                  border: "1px solid #ccc",
                  borderRadius: "4px",
                  fontSize: "16px",
                  color: "#333"
                }}
              />
            </div>
            <button
              type="submit"
              style={{
                width: "100%",
                padding: "0.5rem 1rem",
                backgroundColor: "#0070f3",
                color: "#fff",
                border: "none",
                borderRadius: "4px",
                fontSize: "16px",
                cursor: "pointer",
              }}
            >
              Shorten URL
            </button>
          </form>
          {shortUrl && (
            <div
              style={{
                marginTop: "1.5rem",
                padding: "1rem",
                backgroundColor: "#e6f7ff",
                border: "1px solid #0070f3",
                borderRadius: "4px",
                color: "#333",
                fontSize: "16px",
              }}
            >
              <p>
                Shortened URL:{" "}
                <a
                  href={shortUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{ color: "#0070f3" }}
                >
                  {shortUrl}
                </a>
              </p>
            </div>
          )}
            <button
                onClick={() => (window.location.href = "/dashboard")}
                style={{
                  marginTop: "1rem",
                  padding: "0.5rem 1rem",
                  backgroundColor: "#4CAF50",
                  color: "#fff",
                  border: "none",
                  borderRadius: "4px",
                  fontSize: "16px",
                  cursor: "pointer",
                }}
                >
                My URLs
          </button>
          {error && (
              <div
                style={{
                  position: "relative",
                  marginTop: "1.5rem",
                  padding: "1rem",
                  backgroundColor: "#ffe6e6",
                  border: "1px solid #ff4d4f",
                  borderRadius: "4px",
                  color: "#ff4d4f",
                  fontSize: "16px",
                }}
              >
                {error}
                <button
                  style={{
                    position: "absolute",
                    top: "5px",
                    right: "10px",
                    backgroundColor: "transparent",
                    border: "none",
                    cursor: "pointer",
                    fontSize: "14px",
                    color: "#ff4d4f",
                  }}
                  onClick={() => setError(null)}
                >
                  ×
                </button>
              </div>
          )}

        </div>
      );
    };

    export default ShortenerPage;