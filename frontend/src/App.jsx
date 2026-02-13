import { useEffect, useState, useRef } from "react";
import {
  uploadFile,
  listFiles,
  downloadFile,
  deleteFile
} from "./service/FileService.js";
import { Download, Trash2, UploadCloud } from "lucide-react";

function App() {
  const [selectedFile, setSelectedFile] = useState(null);
  const [files, setFiles] = useState([]);

  const fileInputRef = useRef(null);

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
        setSelectedFile(null);
        if (fileInputRef.current) {
          fileInputRef.current.value = "";
        }
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
        <div className="flex gap-4 mb-10 items-center bg-gray-50 p-4 rounded-lg border border-gray-200">
          <div className="flex-1">
            <input
              type="file"
              ref={fileInputRef}
              onChange={(e) => setSelectedFile(e.target.files[0])}
              className="block w-full text-sm text-gray-500
                file:mr-4 file:py-2 file:px-4
                file:rounded-full file:border-0
                file:text-sm file:font-semibold
                file:bg-blue-50 file:text-blue-700
                hover:file:bg-blue-100"
            />
          </div>

          <button
            onClick={handleUpload}
            disabled={!selectedFile}
            className={`flex items-center gap-2 text-white px-6 py-2 rounded-lg transition font-medium
              ${selectedFile ? 'bg-blue-600 hover:bg-blue-700 shadow-md' : 'bg-gray-400 cursor-not-allowed'}`}
          >
            <UploadCloud size={20} />
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

                    <td className="px-4 py-2">
                      <div className="flex items-center gap-2">
                        <span className="font-medium text-gray-700">{file.filename}</span>

                        {file.path && file.path.startsWith("local://") ? (
                          <span className="px-2 py-0.5 text-xs font-semibold bg-yellow-100 text-yellow-800 rounded border border-yellow-200 shadow-sm flex items-center h-fit">
                            Offline
                          </span>
                        ) : (
                          <span className="px-2 py-0.5 text-xs font-semibold bg-green-100 text-green-800 rounded border border-green-200 shadow-sm flex items-center h-fit">
                            Online
                          </span>
                        )}
                      </div>
                    </td>

                    <td className="px-4 py-2">
                      {formatFileSize(file.size)}
                    </td>

                    <td className="px-4 py-2">
                      {new Date(file.uploadedAt.endsWith("Z") ? file.uploadedAt : file.uploadedAt + "Z").toLocaleString()}
                    </td>

                    <td className="px-4 py-2 text-center flex justify-center space-x-2">
                      <button
                        onClick={() => handleDownload(file.filename)}
                        className="p-2 bg-green-100 text-green-700 rounded-full hover:bg-green-200 transition"
                        title="Download"
                      >
                        <Download size={18} />
                      </button>

                      <button
                        onClick={() => handleDelete(file.filename)}
                        className="p-2 bg-red-100 text-red-700 rounded-full hover:bg-red-200 transition"
                        title="Delete"
                      >
                        <Trash2 size={18} />
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
