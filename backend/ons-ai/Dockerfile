FROM python:3.11-slim
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends tesseract-ocr tesseract-ocr-ara tesseract-ocr-eng && rm -rf /var/lib/apt/lists/*
COPY requirements.txt .
# The hosted low-memory engine uses Tesseract. Local installs retain EasyOCR.
RUN sed '/^easyocr/d' requirements.txt > requirements-hosted.txt && pip install --no-cache-dir -r requirements-hosted.txt
COPY main.py ocr_service.py qr_reader.py ./
ENV OCR_ENGINE=tesseract OMP_THREAD_LIMIT=1 OMP_NUM_THREADS=1 OPENBLAS_NUM_THREADS=1
EXPOSE 8000
CMD ["sh", "-c", "uvicorn main:app --host 0.0.0.0 --port ${PORT:-8000} --workers 1"]
