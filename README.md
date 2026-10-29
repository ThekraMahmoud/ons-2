# ONS | أُنس

المشروع يجمع الواجهة الأمامية والخلفية في مستودع واحد.

- `frontend/`: React وVite، ويشمل الصور والصوتيات والاختبارات.
- `backend/`: Spring Boot، مع الاختبارات وترحيلات قاعدة البيانات.
- `backend/ons-ai/`: خدمة Python للتعرف وقراءة QR، مع `requirements.txt`.

## التشغيل

الواجهة: داخل `frontend` شغّل `npm ci` ثم `npm run dev`. راجع `.env.example` للإعدادات المحلية.

الخلفية: عيّن متغيرات البيئة المشار إليها في `backend/src/main/resources/application.properties` ثم شغّل `./mvnw spring-boot:run` من `backend`.

خدمة Python: ثبّت الحزم من `backend/ons-ai/requirements.txt` داخل بيئة افتراضية، ثم شغّل الخدمة حسب إعدادات التكامل.

كلمات المرور والمفاتيح الخاصة وملفات البيئة المحلية ومجلدات الحزم والبناء لا تُرفع إلى المستودع.

## الاستضافة المجانية

حاوية الباك إند تستخدم البريد عبر webhook المهيأ في `N8N_EMAIL_WEBHOOK_URL` لتجنب منافذ SMTP المحجوبة. التشغيل المحلي يبقى على SMTP افتراضيًا؛ يمكن اختيار `ONS_EMAIL_TRANSPORT=webhook`. حاوية AI تختار Tesseract عربي/إنجليزي ضمن حد ذاكرة 512 MB، ويظل EasyOCR متاحًا محليًا؛ راجع `backend/ons-ai/README.md`.
