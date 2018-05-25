package nadim.com.ndroid.isi;

/**
 * Created by Nadim on 04/05/2018.
 */

class ProcessModel {

    private  String id;
    private  String key;
    private  String category;
    private  String description;
    private  String name;
    private  int version;
    private  String resource;
    private  String deploymentId;

    public ProcessModel(String id, String key, String category, String description, String name, int version, String resource, String deploymentId) {
        this.id = id;
        this.key = key;
        this.category = category;
        this.description = description;
        this.name = name;
        this.version = version;
        this.resource = resource;
        this.deploymentId = deploymentId;
    }

    public ProcessModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }
}
