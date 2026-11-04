import os
import shutil

# 1. Copy branded drawables into watchhub/
shutil.copy("cloned_assets/logo.png", "watchhub/res/drawable-nodpi-v4/logo.png")
shutil.copy("cloned_assets/watch_hub_logo.png", "watchhub/res/drawable-nodpi-v4/watch_hub_logo.png")
shutil.copy("cloned_assets/banner.png", "watchhub/res/drawable/banner.png")

# Mipmaps
densities = ['mdpi', 'hdpi', 'xhdpi', 'xxhdpi', 'xxxhdpi']
for d in densities:
    target_dir = f"watchhub/res/mipmap-{d}-v4"
    if os.path.exists(target_dir):
        shutil.copy(f"cloned_assets/ic_launcher_{d}.png", f"{target_dir}/ic_launcher.png")
        shutil.copy(f"cloned_assets/ic_launcher_fg_{d}.png", f"{target_dir}/ic_launcher_foreground.png")

# 2. Patch resources.arsc to change app_name
arsc_path = "watchhub/resources.arsc"
with open(arsc_path, "rb") as f:
    data = bytearray(f.read())

old_name = b"WatchHub Player"
new_name = b"DRAV TV Player "
pos = data.find(old_name)
if pos != -1:
    data[pos:pos+len(old_name)] = new_name
    print(f"Patched app name at offset {pos} to 'DRAV TV Player '")

with open(arsc_path, "wb") as f:
    f.write(data)

# 3. Clean signatures
meta_dir = "watchhub/META-INF"
if os.path.exists(meta_dir):
    shutil.rmtree(meta_dir)
os.makedirs(meta_dir, exist_ok=True)
if os.path.exists("watchhub/stamp-cert-sha256"):
    os.remove("watchhub/stamp-cert-sha256")

print("Patching complete!")
