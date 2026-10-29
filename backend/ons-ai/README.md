# ONS QR and OCR service

The API remains `/health`, `/scan/qr`, and `/scan/ocr`.

Local installation via `requirements.txt` uses the original EasyOCR Arabic/English model by default.
The Docker image sets `OCR_ENGINE=tesseract` and installs Arabic/English Tesseract models to fit the 512 MB free host. OCR accuracy can differ between engines; validate real document samples before relying on the extracted text.

Start locally: `uvicorn main:app --host 0.0.0.0 --port 8000`.
The container reads the hosting `PORT` variable and runs one worker.
