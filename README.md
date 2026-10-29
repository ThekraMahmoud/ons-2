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
