import { useEffect, useState } from "react";
import {
  uploadFile,
  listFiles,
  downloadFile,
  deleteFile
} from "./service/FileService.js";

function App() {
  const [selectedFile, setSelectedFile] = useState(null);
  const [files, setFiles] = useState([]);

  useEffect(() => {
    fetchFiles();
  }, []);

  const fetchFiles = () => {
    listFiles()
      .then((response) => {
        console.log("FILES:", response.data);
        setFiles(response.data);
      })
      .catch((err) => {
        console.error("LIST FILE ERROR:", err);
      });
  };

  const handleUpload = () => {
    if (!selectedFile) return;

    uploadFile(selectedFile)
      .then(() => {
        alert("File uploaded successfully!");
        fetchFiles();
      })
      .catch((err) => {
        console.error("UPLOAD ERROR:", err);
        alert("Upload failed. Check console.");
      });
  };

  const handleDownload = (filename) => {
    downloadFile(filename).then((response) => {
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
    });
  };

  const handleDelete = (filename) => {
    deleteFile(filename)
      .then(() => {
        alert("Deleted!");
        fetchFiles();
      })
      .catch((err) => console.error(err));
  };

  // Convert bytes to KB/MB
  const formatFileSize = (bytes) => {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  };

  return (
    <div className="min-h-screen bg-gray-100 p-10">
      <div className="max-w-4xl mx-auto bg-white shadow-xl rounded-xl p-8">
        <h1 className="text-3xl font-bold text-center mb-8">
          📁 File Storage System
        </h1>

        {/* Upload Section */}
        <div className="flex gap-4 mb-10">
          <input
            type="file"
            onChange={(e) => setSelectedFile(e.target.files[0])}
            className="border p-2 rounded w-full"
          />

          <button
            onClick={handleUpload}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            Upload
          </button>
        </div>

        {/* File Table */}
        <h2 className="text-xl font-bold mb-4">Available Files</h2>

        <div className="overflow-x-auto shadow-md rounded-lg">
          <table className="min-w-full bg-white rounded-lg">
            <thead className="bg-gray-100 border-b">
              <tr>
                <th className="px-4 py-2 text-left">#</th>
                <th className="px-4 py-2 text-left">Filename</th>
                <th className="px-4 py-2 text-left">Size</th>
                <th className="px-4 py-2 text-left">Uploaded At</th>
                <th className="px-4 py-2 text-center">Actions</th>
              </tr>
            </thead>

            <tbody>
              {files.length === 0 ? (
                <tr>
                  <td
                    colSpan="5"
                    className="text-center py-4 text-gray-500"
                  >
                    No files uploaded yet.
                  </td>
                </tr>
              ) : (
                files.map((file, index) => (
                  <tr
                    key={file.id}
                    className="border-b hover:bg-gray-50 transition"
                  >
                    <td className="px-4 py-2">{index + 1}</td>

                    <td className="px-4 py-2">{file.filename}</td>

                    <td className="px-4 py-2">
                      {formatFileSize(file.size)}
                    </td>

                    <td className="px-4 py-2">
                      {new Date(file.uploadedAt).toLocaleString()}
                    </td>

                    <td className="px-4 py-2 text-center space-x-2">
                      <button
                        onClick={() =>
                          handleDownload(file.filename)
                        }
                        className="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700"
                      >
                        Download
                      </button>

                      <button
                        onClick={() =>
                          handleDelete(file.filename)
                        }
                        className="px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default App;
