import axios from "axios";

const API_URL = "http://localhost:8080/files";

export const uploadFile = (file) => {
  const formData = new FormData();
  formData.append("file", file);

  return axios.post(`${API_URL}/upload`, formData, {
    headers: { "Content-Type": "multipart/form-data" }
  });
};

export const listFiles = () => {
  return axios.get(`${API_URL}/list`);
};

export const downloadFile = (filename) => {
  return axios.get(`${API_URL}/download`, {
    params: { filename },
    responseType: "blob"
  });
};

export const deleteFile = async (filename) => {
    const encoded = encodeURIComponent(filename);
    return axios.delete(`http://localhost:8080/files/delete/${encoded}`);
};


