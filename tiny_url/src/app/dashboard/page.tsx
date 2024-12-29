"use client";

import React, { useState, useEffect} from 'react';
import { format } from "date-fns";

interface Url {
    longUrl : string;
    shortUrl : string;
    createdDate ?: string;
    expirationDate ?: string;
}

const formatDateForUser = (isoString: string): string => {
  return format(new Date(isoString), "dd MMM yyyy, HH:mm");
};

const DashboardPage: React.FC = () => {
    const [urls, setUrls] = useState<Url[]>([]);
    const [username, setUsername] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect( () => {
        const fetchUrls = async () => {
            const username = localStorage.getItem("username");
            if (!username){
                setError("No user logged in");
                return;
            }
            setUsername(username);

            try {
                const response = await fetch(
                    `http://localhost:8080/api/users/${username}/urls`
                );
                if (response.ok){
                    const data: Url[] = await response.json();
                    setUrls(data);
                }
                else{
                    const errorMessage = await response.text();
                    setError(errorMessage);
                }
            } catch (err){
                setError("An error occurred. Please try again later.");
            }
        };
        fetchUrls();

    }, []);

   return (
       <div
         style={{
           backgroundColor: "#fff", // Set the outer background to white
           minHeight: "100vh", // Ensure it covers the full viewport height
           padding: "20px",
         }}
       >
         <div
           style={{
             padding: "20px",
             maxWidth: "1000px", // Center content
             margin: "0 auto",
             backgroundColor: "#fff", // Inner container stays white
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
               Go Back to Shortener
             </button>
           </div>
           {error && <p style={{ color: "red", textAlign: "center" }}>{error}</p>}
           {!error && urls.length === 0 && <p style={{ textAlign: "center" }}>No URLs found.</p>}
           <div
             style={{
               display: "grid",
               gridTemplateColumns: "1fr 1fr 1fr 1fr", // Four equal columns
               gap: "10px",
             }}
           >
             {/* Header Row */}
             <div
               style={{
                 fontWeight: "bold",
                 borderBottom: "2px solid #ccc",
                 padding: "10px",
                 backgroundColor: "#4CAF50", // Green background for header
                 color: "#fff", // White text for contrast
                 textAlign: "center",
               }}
             >
               Long URL
             </div>
             <div
               style={{
                 fontWeight: "bold",
                 borderBottom: "2px solid #ccc",
                 padding: "10px",
                 backgroundColor: "#4CAF50",
                 color: "#fff",
                 textAlign: "center",
               }}
             >
               Short URL
             </div>
             <div
               style={{
                 fontWeight: "bold",
                 borderBottom: "2px solid #ccc",
                 padding: "10px",
                 backgroundColor: "#4CAF50",
                 color: "#fff",
                 textAlign: "center",
               }}
             >
               Created Date
             </div>
             <div
               style={{
                 fontWeight: "bold",
                 borderBottom: "2px solid #ccc",
                 padding: "10px",
                 backgroundColor: "#4CAF50",
                 color: "#fff",
                 textAlign: "center",
               }}
             >
               Expiration Date
             </div>

             {/* Data Rows */}
             {urls.map((url, index) => (
               <React.Fragment key={index}>
                 <div
                   style={{
                     padding: "10px",
                     wordBreak: "break-word",
                     display: "flex",
                     alignItems: "center",
                     justifyContent: "center",
                     textAlign: "center",
                   }}
                 >
                   <a
                     href={url.longUrl}
                     target="_blank"
                     rel="noopener noreferrer"
                     style={{
                       textDecoration: "none",
                       color: "#333",
                     }}
                     onMouseOver={(e) => (e.currentTarget.style.textDecoration = "underline")}
                     onMouseOut={(e) =>
                       (e.currentTarget.style.textDecoration = "none")
                     }
                   >
                     {url.longUrl}
                   </a>
                 </div>
                 <div
                   style={{
                     padding: "10px",
                     wordBreak: "break-word",
                     display: "flex",
                     alignItems: "center",
                     justifyContent: "center",
                     textAlign: "center",
                   }}
                 >
                   <a
                     href={`${url.shortUrl}`}
                     target="_blank"
                     rel="noopener noreferrer"
                     style={{
                       textDecoration: "none",
                       color: "#333",
                     }}
                     onMouseOver={(e) => (e.currentTarget.style.textDecoration = "underline")}
                     onMouseOut={(e) =>
                       (e.currentTarget.style.textDecoration = "none")
                     }
                   >
                     {url.shortUrl}
                   </a>
                 </div>
                 <div
                   style={{
                     padding: "10px",
                     whiteSpace: "nowrap",
                     color: "#333",
                     display: "flex",
                     alignItems: "center",
                     justifyContent: "center",
                     textAlign: "center",
                   }}
                 >
                   {formatDateForUser(url.createdDate) || "N/A"}
                 </div>
                 <div
                   style={{
                     padding: "10px",
                     whiteSpace: "nowrap",
                     color: "#333",
                     display: "flex",
                     alignItems: "center",
                     justifyContent: "center",
                     textAlign: "center",
                   }}
                 >
                   {formatDateForUser(url.expirationDate) || "N/A"}
                 </div>
               </React.Fragment>
             ))}
           </div>
         </div>
       </div>
     );
   };

   export default DashboardPage;
