# How to Add Image URL Support to Cars

This update adds a new field to the car entries allowing you to specify image URLs. 
This enables displaying car images from remote sources in the car details dialog.

## Step 1: Update the Database Schema

Run the SchemaUpdater tool to add the new image_url column to the cars table:

```
cd d:\You know what PROJECTZZ\RENTOPS-AI
java utils.SchemaUpdater
```

## Step 2: Add Image URLs to Cars

1. Log in as admin
2. Go to the Cars section
3. Select a car and click "Edit Car"
4. You'll see a new "Image URL" field
5. Enter a valid image URL (starting with http:// or https://)
   - For a single image: direct URL to the image (e.g., https://example.com/cars/honda_civic.jpg)
   - For multiple images: URL to a folder containing images (e.g., https://example.com/cars/honda_civic/)

## Step 3: View Images

Users can now see the car images when they:
1. Log in as a regular user
2. Browse available cars
3. Double-click on a car or click the "View Car Details" button

## Tips for Image URLs

- Use high-quality, consistently sized images (recommended: 800x600px)
- For galleries, name your images with standard names like 1.jpg, 2.jpg, front.jpg, etc.
- If providing a folder URL, make sure it ends with a slash (/)
- You can also use direct image URLs for single images

## Troubleshooting

- If images don't appear, check that the URL is accessible and contains image files
- Verify there are no typos in the URL
- Make sure your server allows external image loading