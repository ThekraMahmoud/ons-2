from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import JSONResponse
from qr_reader import read_qr
from ocr_service import read_text

app = FastAPI(
    title="ONS AI Service",
    description="QR and OCR service for ONS",
    version="1.0.0"
)

@app.get("/health")
def health():
    return {"status": "ok", "service": "ons-ai"}

@app.post("/scan/qr")
async def scan_qr(file: UploadFile = File(...)):
    image_bytes = await file.read()
    if not image_bytes:
        raise HTTPException(status_code=400, detail="Uploaded file is empty.")

    result = read_qr(image_bytes)
    return JSONResponse(
        status_code=200 if result["success"] else 422,
        content=result
    )

@app.post("/scan/ocr")
async def scan_ocr(file: UploadFile = File(...)):
    image_bytes = await file.read()
    if not image_bytes:
        raise HTTPException(status_code=400, detail="Uploaded file is empty.")

    result = read_text(image_bytes)
    return JSONResponse(
        status_code=200 if result["success"] else 422,
        content=result
    )
