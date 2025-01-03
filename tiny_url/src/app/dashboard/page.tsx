"use client";

import React, { useState, useEffect, element } from "react";
import { format } from "date-fns";

interface Url {
    longUrl: string;
    shortUrl: string;
    createdDate?: string;
    expirationDate?: string;
}

const formatDateForUser = (isoString: string | null | undefined): string => {
    if (!isoString || isNaN(new Date(isoString).getTime())) {
        return "Never"; // Fallback for invalid or null/undefined values
    }
    try {
        return format(new Date(isoString), "dd MMM yyyy, HH:mm");
    } catch (error) {
        console.error("Invalid date format:", isoString);
        return "Invalid Date"; // Fallback for unexpected errors
    }
};


const DashboardPage: React.FC = () => {
    const [urls, setUrls] = useState<Url[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [hoverData, setHoverData] = useState<{
      shortUrl: string;
      clickCount: number;
      lastClicked: string | null;
    } | null>(null);

    useEffect(() => {
        const fetchUrls = async () => {
            const token = localStorage.getItem("token");
            if (!token) {
                setError("You are not logged in. Redirecting to login...");
                setTimeout(() => (window.location.href = "/login"), 2000);
                return;
            }

            try {
                const response = await fetch(`http://localhost:8080/api/users/urls`,{
                    method: "GET",
                    headers: { "Authorization": `Bearer ${token}` },
                });
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
        const token = localStorage.getItem("token");
        if (!token) {
            setError("You are not logged in.");
            return;
        }

        try {
            // Extract alias from the short URL
            const oldAlias = shortUrl.replace("http://localhost:8080/api/url/", "");
            const response = await fetch(
                `http://localhost:8080/api/users/urls/${oldAlias}?newAlias=${newAlias}`,
                {
                    method: "PUT",
                    headers: { "Content-Type": "application/json",
                                "Authorization": `Bearer ${token}`,
                    },
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
                const refreshedResponse = await fetch(`http://localhost:8080/api/users/urls`,{
                    headers: { "Authorization": `Bearer ${token}` },
                });
                if (refreshedResponse.ok) {
                    const refreshedData: Url[] = await refreshedResponse.json();
                    setUrls(refreshedData);
                }
                else {
                    const errorMessage = await response.text();
                    setError(errorMessage || "Failed to rename the URL.");
                }
            }
        } catch (err) {
            console.error("Error renaming URL:", err);
            setError("An error occurred. Please try again later.");
        }
    };

    const handleUpdateLongUrl = async (shortUrl: string, newLongUrl: string) => {
        const token = localStorage.getItem("token");
        if (!token) {
            setError("You are not logged in.");
            return;
        }
        try {
            // Extract alias
            const alias = shortUrl.replace("http://localhost:8080/api/url/", "");
            const response = await fetch(
                `http://localhost:8080/api/users/urls/${alias}/update`,
                  {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`,
                    },
                    body: JSON.stringify({ newLongUrl }),
                  }
            );

            if (response.ok) {
                setSuccess("Long URL updated successfully");
                // Clear success message after 3 seconds
                setTimeout(() => { setSuccess(null);}, 3000);
                // Refresh the URLs
                const refreshedResponse = await fetch(`http://localhost:8080/api/users/urls`, {
                    headers: { "Authorization": `Bearer ${token}` },
                });
                if (refreshedResponse.ok) {
                    const refreshedData: Url[] = await refreshedResponse.json();
                    setUrls(refreshedData);
                }
            }
            else {
                const errorMessage = await response.text();
                setError(errorMessage || "Failed to update the long URL");
            }
        } catch (err) {
            console.error("Error updating long URL:", err);
            setError("An error occurred while updating the long URL.");
        }
    };

    const handleDeleteShortUrl = async (shortUrl: string) => {
        const token = localStorage.getItem("token");
        if (!token) {
            setError("You are not logged in.");
            return;
        }
        try {
            const alias = shortUrl.replace("http://localhost:8080/api/url/", ""); // Extract alias
            const response = await fetch(
                `http://localhost:8080/api/users/urls/${alias}`,
                {
                    method: "DELETE",
                    headers: { "Content-Type": "application/json",
                               "Authorization": `Bearer ${token}`,
                    },
                }
            );

            if (response.ok) {
              setSuccess("Short URL deleted successfully.");
              // Clear success message after 3 seconds
              setTimeout(() => { setSuccess(null);}, 3000);
              // Refresh the list of URLs
              const refreshedResponse = await fetch(`http://localhost:8080/api/users/urls`, {
                  headers: { "Authorization": `Bearer ${token}` },
              });
              if (refreshedResponse.ok) {
                const refreshedData: Url[] = await refreshedResponse.json();
                setUrls(refreshedData);
              }
            }
            else {
                const errorMessage = await response.text();
                setError(errorMessage || "Failed to delete the short URL.");
            }
            } catch (err) {
                console.error("Error deleting short URL:", err);
                setError("An error occurred while deleting the short URL.");
            }
    };

    const handleHoverInfo = async (shortUrl: string) => {
        const token = localStorage.getItem("token");
        if (!token) {
            setError("You are not logged in.");
            return;
        }
        const alias = shortUrl.replace("http://localhost:8080/api/url/", ""); // Extract alias
        try {
            // Fetch click count  and last clicked data simultaneously
            const [clickCountResponse, lastClickedResponse] = await Promise.all([
                fetch(`http://localhost:8080/api/users/urls/${alias}/clicks`, {
                    method: "GET",
                    headers: { "Content-Type": "application/json",
                               "Authorization": `Bearer ${token}`,
                    },
                }),
                fetch(`http://localhost:8080/api/users/urls/${alias}/last-clicked`, {
                    method: "GET",
                    headers: { "Content-Type": "application/json",
                               "Authorization": `Bearer ${token}`,
                    },
                }),
            ]);

            const clickCountData = await clickCountResponse.json();
            const lastClickedData = await lastClickedResponse.json();

            // For debug
            console.log("Setting hover data:", {
                shortUrl,
                clickCount: clickCountData.message,
                lastClicked: lastClickedData.message,
            });

            const formattedLastClicked = formatDateForUser(lastClickedData.message.replace("Last clicked time: ", ""))

            setHoverData({
               shortUrl,
               clickCount: clickCountData.message.replace("Click count: ",""),
               lastClicked: formattedLastClicked,
            });
        } catch (err) {
            console.error("Error fetching info data:", err);
            setHoverData(null); // clear hover data on error
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
            <p style={{ textAlign: "center", color: "#333" }}>No URLs Found</p>
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
                      gap: "15px", // Horizontal spacing between input and button
                      marginBottom: "10px", // Vertical spacing between forms
                      width: "100%", // Ensures both forms take equal space
                    }}
                    onSubmit={(e) => {
                      e.preventDefault();
                      const newAlias = (e.target as any).newAlias.value;
                      handleRename(url.shortUrl, newAlias);
                      (e.target as any).newAlias.value = ""; // Clear input field
                    }}
                  >
                    <input
                      type="text"
                      name="newAlias"
                      placeholder="New Alias"
                      style={inputStyle}
                    />
                    <button
                      type="submit"
                      style={buttonStyle}
                    >
                      Rename
                    </button>
                  </form>
                  <form
                    style={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      gap: "15px", // Horizontal spacing between input and button
                      marginBottom: "10px", // Vertical spacing between forms
                      width: "100%", // Ensures both forms take equal space
                    }}
                    onSubmit={(e) => {
                      e.preventDefault();
                      const newLongUrl = (e.target as any).newLongUrl.value;
                      handleUpdateLongUrl(url.shortUrl, newLongUrl);
                      (e.target as any).newLongUrl.value = ""; // Clear the input field
                    }}
                  >
                    <input
                      type="text"
                      name="newLongUrl"
                      placeholder="New Long URL"
                      style={inputStyle}
                    />
                    <button
                      type="submit"
                      style={buttonStyle}
                    >
                      Update
                    </button>
                  </form>
                  <button
                    style={{
                        ...buttonStyle,
                        backgroundColor: "#FF4C4C",
                        color: "#fff",
                        border: "none",
                        borderRadius: "4px",
                        padding: "5px 10px",
                        cursor: "pointer",
                        marginTop: "10px",
                    }}
                        onClick={() => handleDeleteShortUrl(url.shortUrl)}
                      >
                        Delete
                      </button>
                      <span
                      style={{
                        marginLeft: "10px",
                        color: "#0070f3",
                        cursor: "pointer",
                        textDecoration: "underline",
                      }}
                      onMouseEnter={() => handleHoverInfo(url.shortUrl)}
                      onMouseLeave={() => setHoverData(null)}
                    >
                      Info
                    </span>
                    {hoverData && hoverData.shortUrl === url.shortUrl && (
                      <div
                        style={{
                          position: "absolute",
                          backgroundColor: "#fff",
                          border: "1px solid #ccc",
                          borderRadius: "5px",
                          padding: "10px",
                          boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.1)",
                          marginTop: "5px", // Add some spacing between "Info" and the grid
                          zIndex: 10,
                          color: "#333",
                        }}
                      >
                        <p>Click Count: {hoverData?.clickCount ?? "Loading..."}</p>
                        <p>Last Clicked: {hoverData?.lastClicked ?? "Loading..."}</p>
                      </div>
                    )}

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

    const inputStyle = {
        padding: "10px", // Adequate padding for placeholder text
        border: "1px solid #ccc",
        borderRadius: "4px",
        textAlign: "center",
        color: "#333",
        width: "300px", // Fixed width to ensure consistency
        height: "40px", // Fixed height for uniform size
        margin: "5px 0", // Vertical margin for spacing between grids
    };

    const buttonStyle = {
        backgroundColor: "#4CAF50",
        color: "#fff",
        border: "none",
        borderRadius: "4px",
        padding: "10px 20px",
        cursor: "pointer",
        height: "40px", // Match height with inputs
        marginTop: "10px", // Vertical spacing between grids and buttons
    };

  export default DashboardPage;
