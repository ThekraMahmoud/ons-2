import cv2
import numpy as np

def read_qr(image_bytes: bytes) -> dict:
    try:
        encoded = np.frombuffer(image_bytes, dtype=np.uint8)
        image = cv2.imdecode(encoded, cv2.IMREAD_COLOR)

        if image is None:
            return {
                "success": False,
                "cardCode": None,
                "message": "The uploaded file is not a readable image."
            }

        detector = cv2.QRCodeDetector()
        decoded_values = []

        try:
            ok, decoded_info, points, _ = detector.detectAndDecodeMulti(image)
            if ok and decoded_info:
                decoded_values = [
                    value.strip()
                    for value in decoded_info
                    if value and value.strip()
                ]
        except (cv2.error, AttributeError, TypeError):
            pass

        if not decoded_values:
            value, points, _ = detector.detectAndDecode(image)
            if value and value.strip():
                decoded_values = [value.strip()]

        if not decoded_values:
            return {
                "success": False,
                "cardCode": None,
                "message": "No QR code was detected in the image."
            }

        return {
            "success": True,
            "cardCode": decoded_values[0],
            "allCodes": decoded_values,
            "message": "QR code read successfully."
        }

    except Exception as exc:
        return {
            "success": False,
            "cardCode": None,
            "message": f"QR processing failed: {type(exc).__name__}"
        }
