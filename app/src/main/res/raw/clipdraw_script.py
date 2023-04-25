import argparse
import torch
import clip
from PIL import Image

# Parse the input text using argparse
parser = argparse.ArgumentParser()
parser.add_argument('input_text', type=str, help='input text to generate image')
args = parser.parse_args()

# Load the CLIP model and the associated preprocessing functions
device = "cuda" if torch.cuda.is_available() else "cpu"
model, preprocess = clip.load("ViT-B/32", device)

# Convert the input text into a PyTorch tensor using the CLIP preprocessing function
text_inputs = clip.tokenize([args.input_text]).to(device)

# Generate the image using the CLIPDraw model
with torch.no_grad():
    z = torch.randn([1, 512]).to(device)
    image_features = model.encode_image(z)
    text_features = model.encode_text(text_inputs)
    target_features = (image_features + text_features) / 2
    generated_image = clip.draw(model, target_features, z)

# Save the generated image to a file
generated_image = generated_image[0].cpu().numpy().transpose(1, 2, 0)
generated_image = ((generated_image + 1) / 2 * 255).astype('uint8')
img = Image.fromarray(generated_image)
img.save('output.png')
