"use client";

import React, { useState, useEffect } from "react";
import { format } from "date-fns";

interface Url {
    longUrl: string;
    shortUrl: string;
    createdDate?: string;
    expirationDate?: string;
}

const formatDateForUser = (isoString: string): string => {
    return format(new Date(isoString), "dd MMM yyyy, HH:mm");
};

const DashboardPage: React.FC = () => {
    const [urls, setUrls] = useState<Url[]>([]);
    const [username, setUsername] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    useEffect(() => {
        const fetchUrls = async () => {
            const username = localStorage.getItem("username");
            if (!username) {
                setError("No user logged in");
                return;
            }
            setUsername(username);

            try {
                const response = await fetch(`http://localhost:8080/api/users/${username}/urls`);
                if (response.ok) {
                    const data: Url[] = await response.json();
                    setUrls(data);
                }
                else {
                    const errorMessage = await response.text();
                    setError(errorMessage);
                }
            } catch (err) {
                setError("An error occurred. Please try again later.");
            }
        };
        fetchUrls();
    }, []);

    const handleRename = async (shortUrl: string, newAlias: string) => {
        if (!username) {
            setError("No user logged in");
            return;
        }

        try {
            // Extract alias from the short URL
            const baseUrl = "http://localhost:8080/api/url/";
            const oldAlias = shortUrl.startsWith(baseUrl) ? shortUrl.substring(baseUrl.length) : shortUrl;
            const response = await fetch(
                `http://localhost:8080/api/users/${username}/urls/${oldAlias}?newAlias=${newAlias}`,
                {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                }
            );

            if (!response.ok) {
                const errorMessage = await response.text();
                setError(errorMessage || "Failed to rename the URL.");
            }
            else {
                setSuccess("Short URL renamed successfully.");
                // Clear success message after 3 seconds
                setTimeout(() => { setSuccess(null);}, 3000);
                // Refresh the URLs
                const refreshedResponse = await fetch(`http://localhost:8080/api/users/${username}/urls`);
                if (refreshedResponse.ok) {
                    const refreshedData: Url[] = await refreshedResponse.json();
                    setUrls(refreshedData);
                }
            }
        } catch (err) {
            setError("An error occurred. Please try again later.");
        }
    };

  return (
      <div
        style={{
          backgroundColor: "#fff",
          minHeight: "100vh",
          padding: "20px",
        }}
      >
        <div
          style={{
            padding: "20px",
            maxWidth: "1000px",
            margin: "0 auto",
            backgroundColor: "#fff",
          }}
        >
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginBottom: "20px",
            }}
          >
            <h1 style={{ fontWeight: "bold", fontSize: "2rem", color: "#333" }}>
              Created URLs
            </h1>
            <button
              onClick={() => (window.location.href = "/shortener")}
              style={{
                backgroundColor: "#0070f3",
                color: "#fff",
                border: "none",
                borderRadius: "5px",
                padding: "10px 20px",
                fontSize: "1rem",
                cursor: "pointer",
                boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.1)",
              }}
            >
              Shorten Another
            </button>
          </div>
          {error && <p style={{ color: "red", textAlign: "center" }}>{error}</p>}
          {success && (
            <p style={{ color: "green", textAlign: "center" }}>{success}</p>
          )}
          {!error && urls.length === 0 && (
            <p style={{ textAlign: "center" }}>No URLs found.</p>
          )}
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr 1fr 1fr 1fr", // Five equal columns
              gap: "10px",
            }}
          >
            {/* Header Row */}
            <div style={headerStyle}>Long URL</div>
            <div style={headerStyle}>Short URL</div>
            <div style={headerStyle}>Created Date</div>
            <div style={headerStyle}>Expiration Date</div>
            <div style={headerStyle}>Actions</div>

            {/* Data Rows */}
            {urls.map((url, index) => (
              <React.Fragment key={index}>
                <div style={dataStyle}>
                  <a
                    href={url.longUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{
                      textDecoration: "none",
                      color: "#333",
                    }}
                  >
                    {url.longUrl}
                  </a>
                </div>
                <div style={dataStyle}>
                  <a
                    href={`${url.shortUrl}`}
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{
                      textDecoration: "none",
                      color: "#333",
                    }}
                  >
                    {url.shortUrl}
                  </a>
                </div>
                <div style={dataStyle}>
                  {formatDateForUser(url.createdDate) || "N/A"}
                </div>
                <div style={dataStyle}>
                  {formatDateForUser(url.expirationDate) || "N/A"}
                </div>
                <div style={{dataStyle}}>
                  <form
                    style={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      gap: "10px",
                    }}
                    onSubmit={(e) => {
                      e.preventDefault();
                      const form = e.target as any;
                      const newAlias = (e.target as any).newAlias.value;
                      handleRename(url.shortUrl, newAlias);
                      form.newAlias.value = ""; // Clear the input field
                    }}
                  >
                    <input
                      type="text"
                      name="newAlias"
                      placeholder="New Alias"
                      style={{
                        marginRight: "10px",
                        padding: "5px",
                        border: "1px solid #ccc",
                        borderRadius: "4px",
                        textAlign: "center",
                        color: "#333",
                        marginTop: "15px",
                      }}
                    />
                    <button
                      type="submit"
                      style={{
                        backgroundColor: "#4CAF50",
                        color: "#fff",
                        border: "none",
                        borderRadius: "4px",
                        padding: "5px 10px",
                        cursor: "pointer",
                        marginTop: "15px",
                      }}
                    >
                      Rename
                    </button>
                  </form>
                </div>
              </React.Fragment>
            ))}
          </div>
        </div>
      </div>
    );
  };

  const headerStyle = {
    fontWeight: "bold",
    borderBottom: "2px solid #ccc",
    padding: "10px",
    backgroundColor: "#4CAF50",
    color: "#fff",
    textAlign: "center",
  };

  const dataStyle = {
    padding: "10px",
    wordBreak: "break-word",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    textAlign: "center",
    color: "#333",
  };

  export default DashboardPage;
