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
           padding: "20px",
           maxWidth: "800px",
           margin: "0 auto",
           backgroundColor: "#f7f7f7",
         }}
       >
         <h1 style={{ fontWeight: "bold", padding: "8px", color: "#333" }}>Created URLs</h1>
         {error && <p style={{ color: "red" }}>{error}</p>}
         {!error && urls.length === 0 && <p>No URLs found.</p>}
         <table
           style={{
             width: "100%",
             borderCollapse: "collapse",
             marginTop: "20px",
           }}
         >
           <thead style={{fontWeight: "bold", color: "#333"}}>
             <tr>
               <th style={{ borderBottom: "1px solid #ccc", padding: "8px", fontWeight: "bold", color: "#333"}}>
                 Long URL
               </th>
               <th style={{ borderBottom: "1px solid #ccc", padding: "8px", fontWeight: "bold", color: "#333" }}>
                 Short URL
               </th>
               <th style={{ borderBottom: "1px solid #ccc", padding: "8px", fontWeight: "bold", color: "#333" }}>
                 Created Date
               </th>
               <th style={{ borderBottom: "1px solid #ccc", padding: "8px", fontWeight: "bold", color: "#333" }}>
                 Expiration Date
               </th>
             </tr>
           </thead>
           <tbody>
             {urls.map((url, index) => (
               <tr key={index}>
                 <td style={{ padding: "8px", color: "#333" }}>{url.longUrl}</td>
                 <td style={{ padding: "8px" }}>
                   <a style={{ padding: "8px", color: "#333" }}
                     href={`http://localhost:8080/${url.shortUrl}`}
                     target="_blank"
                     rel="noopener noreferrer"
                   >
                     {url.shortUrl}
                   </a>
                 </td>
                 <td style={{ padding: "8px", color: "#333" }}>{formatDateForUser(url.createdDate) || "N/A"}</td>
                 <td style={{ padding: "8px", color: "#333" }}>{formatDateForUser(url.expirationDate) || "N/A"}</td>
               </tr>
             ))}
           </tbody>
         </table>
       </div>
     );
};

   export default DashboardPage;